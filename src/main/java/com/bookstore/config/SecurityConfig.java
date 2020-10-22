package com.bookstore.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.bookstore.service.impl.UserSecurityService;
import com.bookstore.utility.SecurityUtility;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled=true,securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	DataSource dataSource;
	@Autowired
	BCryptPasswordEncoder bCryptEncoder;
	
		private static String [] PUBLIC_MATCHERS = {
				"/**",
				"/css/**",
				"/js/**",
				"/image/**",
				"/fonts/**",
				"/login",
				"/new-account",
				"/my-account",
				"/forget-password",
				"/"
		};
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.authorizeRequests()
				.antMatchers(PUBLIC_MATCHERS)
				.permitAll()
				.anyRequest()
				.authenticated();
			
			http.csrf().disable().cors().disable();
			
			http
			.formLogin()
			.loginPage("/login")
			.failureUrl("/login?error=true")
			.defaultSuccessUrl("/")
			.permitAll();
			
			http
			.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessUrl("/?logout=true").deleteCookies("remember-me").permitAll()
			.and()
			.rememberMe();
			
			System.out.println("===========login configuration===========");
		}
		
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.jdbcAuthentication()
			.usersByUsernameQuery("SELECT USERNAME,PASSWORD,ENABLED "
					+ "FROM USER WHERE USERNAME = ?")
			.authoritiesByUsernameQuery("select r.name, u.username from user u , role r " + 
					"where u.id in (select ur.user_id from user_role ur where ur.role_id=r.role_id) and u.username=?")
			.dataSource(dataSource)
			.passwordEncoder(bCryptEncoder);
			
		}
		
	}

