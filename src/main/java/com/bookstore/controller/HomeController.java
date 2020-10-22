package com.bookstore.controller;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookstore.domain.User;
import com.bookstore.domain.security.PasswordResetToken;
import com.bookstore.domain.security.Role;
import com.bookstore.domain.security.UserRole;
import com.bookstore.service.UserService;
import com.bookstore.service.impl.UserSecurityService;
import com.bookstore.utility.MailConstructor;
import com.bookstore.utility.SecurityUtility;

@Controller
public class HomeController {

	@GetMapping("/")
	public String displayHomePage() {
		return "index";
	}
	/*
	 * 
	 * @GetMapping("/my-account") public String displayMyAccountPage() { return
	 * "my-account"; }
	 */
	
	@Autowired
	JavaMailSender mailSender;
	 
	@Autowired
	MailConstructor mailConstructor;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserSecurityService userSecurityService;
	
	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("classActiveLogin",true);
		return "my-account";
	}
	/*
	 * @GetMapping("/forget-password") public String forgetPassword(Model model){
	 * model.addAttribute("classActiveForgetPassword",true); return "my-account"; }
	 */
	
	@PostMapping("/new-account")
	public String saveNewAccount(HttpServletRequest request, 
			@ModelAttribute("email") String userEmail,
			@ModelAttribute("username") String username,
			Model model) throws Exception{
		
		model.addAttribute("classActiveNewAccount",true);
		
		model.addAttribute("email",userEmail);
		model.addAttribute("username",username);
		
		if(userService.findByUsername(username)!= null) {
			model.addAttribute("usernameExists",true);
			return "my-account";
			}
		if(userService.findByEmail(userEmail)!= null) {
			model.addAttribute("emailExists",true);
			return "my-account";
			}
		
		User user = new User();
		
		user.setUsername(username);
		user.setEmail(userEmail);
		
		String password = SecurityUtility.randomPassword();
		System.out.println("======= new user password before encode = " + password);
		
		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		
		System.out.println("======= new user password after encode = " + encryptedPassword);
		
		user.setPassword(encryptedPassword);
		
		Role role = new Role();
		
		role.setRoleId(1);
		role.setName("ROLE_USER");
		
		Set<UserRole> userRoles = new HashSet<>();
		
		userRoles.add(new UserRole(role,user));
		
		userService.createUser(user,userRoles);
		
		String token = UUID.randomUUID().toString();
		
		userService.createPasswordResetTokenForUser(user, token);
		
		String appURL = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
		
		SimpleMailMessage email = mailConstructor.constructResetTokenEmail(appURL,request.getLocale()
																			,token,user,password);
		mailSender.send(email);
		
		model.addAttribute("emailSent",true);
		
		return"my-account";	
	}
	
	
	@GetMapping("/new-account")
	public String newAccount(Model model,
			Locale locale,
			@RequestParam ("token") String token) {
		
		PasswordResetToken passToken = userService.getPasswordResetToken(token);
		
		if(passToken == null) {
			String message = "Invalid token..";
			model.addAttribute("invalidTokenMessage",message);
			
			return"redirect:badRequest";
		}
		
		User user = passToken.getUser();
		
		String username = user.getUsername();
		
		UserDetails userDetails = userSecurityService.loadUserByUsername(username);
		
		Authentication authentication = new UsernamePasswordAuthenticationToken
				(userDetails, userDetails.getPassword(),userDetails.getAuthorities());
		
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		model.addAttribute("user",user);
		
		model.addAttribute("classActiveEdit",true);
		return "my-profile";
	}
	
	
	/*
	 * @PostMapping("/forget-password") public String displayForgetPasswordPage(User
	 * user, Model model) { model.addAttribute("classActiveForgetPassword",true);
	 * return "my-account"; }
	 */
	
	@PostMapping("/forget-password")
	public String forgetPassword(
			HttpServletRequest request,
			@ModelAttribute("email") String email,
			Model model
			) {

		model.addAttribute("classActiveForgetPassword", true);
		
		User user = userService.findByEmail(email);
		
		if (user == null) {
			model.addAttribute("emailNotExist", true);
			return "my-account";
		}
		
		String password = SecurityUtility.randomPassword();
		
		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		user.setPassword(encryptedPassword);
		
		userService.save(user);
		
		String token = UUID.randomUUID().toString();
		userService.createPasswordResetTokenForUser(user, token);
		
		String appUrl = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
		
		SimpleMailMessage newEmail = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user, password);
		
		mailSender.send(newEmail);
		
		model.addAttribute("forgetPasswordEmailSent", "true");
		
		return "my-account";
	}
	
	/*******************************/
	
	@GetMapping("/my-profile")
	public String displayMyProfilePage(User user, Model model) {
		return "my-profile";
	}
	
	
	@PostMapping("/update-user-info")
	public String saveUpdatedUserDetails(User user, Model model) {
		return "my-profile";
	}
	
	
	@GetMapping("/my-shopping-cart")
	public String displayMyShoppingCartPage() {
		return "my-shopping-cart";
	}
	
	@GetMapping("/logout")
	public String logoutPage() {
		return "logout";
	}
	
	
}
