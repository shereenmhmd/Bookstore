package com.bookstore.service.impl;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.Repository.PasswordResetTokenRepository;
import com.bookstore.Repository.RoleRepository;
import com.bookstore.Repository.UserRepository;
import com.bookstore.domain.User;
import com.bookstore.domain.security.PasswordResetToken;
import com.bookstore.domain.security.UserRole;
import com.bookstore.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	@Override
	public PasswordResetToken getPasswordResetToken(String token) {
		
		return passwordResetTokenRepository.findByToken(token);
	}

	@Override
	public void createPasswordResetTokenForUser(final User user, final String token) {
		
		final PasswordResetToken myToken = new PasswordResetToken(token, user);
		
		passwordResetTokenRepository.save(myToken);
		
	}
	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
		
	}
	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
		
	}
	
	@Override
	public
	User createUser(User user, Set<UserRole> userRoles)  {
		
		User checkUser = userRepository.findByUsername(user.getUsername());
		
		if (checkUser != null) {
		LOG.info(user.getUsername() + " user already exist ");
		}
		else {
			for (UserRole ur:userRoles) {
					roleRepository.save(ur.getRole());
				}
			
			user.getUserRoles().addAll(userRoles);
			checkUser = userRepository.save(user);
			}
		return checkUser;
	}

	public User save (User user) {
		return userRepository.save(user);
	}

	
}
