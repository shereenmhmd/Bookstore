package com.bookstore;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bookstore.domain.User;
import com.bookstore.domain.security.Role;
import com.bookstore.domain.security.UserRole;
import com.bookstore.service.UserService;
import com.bookstore.utility.SecurityUtility;

@SpringBootApplication
public class BookstoreApplication implements CommandLineRunner {

	
	@Autowired
	private UserService userService;
	
	public static void main(String[] args) {
		SpringApplication.run(BookstoreApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		/*
		 * User user1= new User();
		 * 
		 * user1.setFirstName("Hala"); user1.setLastName("Zahran");
		 * user1.setUsername("hala");
		 * user1.setPassword(SecurityUtility.passwordEncoder().encode("hala"));
		 * user1.setEmail("shereen.zahran06@gmail.com");
		 * 
		 * Role role1 = new Role();
		 * 
		 * role1.setRoleId(1); role1.setName("ROLE_USER");
		 * 
		 * Set<UserRole> userRoles = new HashSet<>();
		 * 
		 * userRoles.add(new UserRole(role1,user1));
		 * 
		 * userService.createUser(user1,userRoles);
		 */
	}

}
