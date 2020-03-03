package es.ucm.fdi.iw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security configuration.
 * 
 * Most security configuration will appear in this file, but according to 
 * https://spring.io/guides/topicals/spring-security-architecture/, it is not
 * a bad idea to also use method security (via @Secured annotations in methods) 
 * 
 * @author mfreire
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	/**
	 * Main security configuration.
	 * 
	 * The first rule that matches will be followed - so if a rule decides to grant access
	 * to a resource, a later rule cannot deny that access, and vice-versa.
	 * 
	 * To disable security entirely, just add an .antMatchers("**").permitAll() 
	 * as a first rule. Note that this may break an application that expects to have
	 * login information available.
	 */
	protected void configure(HttpSecurity http) throws Exception {
	    http
	        .authorizeRequests()
	            .antMatchers("/css/**", "/js/**", "/img/**", "/", "/error").permitAll()
	            // .antMatchers("/clase/enter").permitAll() 		 <-- only when logging in to vote 
	            .antMatchers("/admin/**").hasRole("ADMIN")		// <-- administration
	            .anyRequest().authenticated()
	            .and()
	        .formLogin()
	        	.permitAll().successHandler(loginSuccessHandler);// <-- called when login Ok; can redirect
	}
	
	/**
	 * Declares a PasswordEncoder bean.
	 * 
	 * This allows you to write, in any part of Spring-managed code, 
	 * `@Autowired PasswordEncoder passwordEncoder`, and have it initialized
	 * with the result of this method. 
	 */
	@Bean
	public PasswordEncoder getPasswordEncoder() {
		// by default in Spring Security 5, a wrapped new BCryptPasswordEncoder();
		return PasswordEncoderFactories.createDelegatingPasswordEncoder(); 
	}	
	
	/**
	 * Declares a springDataUserDetailsService bean.
	 * 
	 * This is used to translate from Spring Security users to in-application users.
	 */
	@Bean
	public IwUserDetailsService springDataUserDetailsService() {
		return new IwUserDetailsService();
	} 
	
	/**
	 * Declares an AuthenticationManager bean.
	 * 
	 *  This can be used to auto-login into the site after creating new users, for example.
	 */
	 @Bean
	 @Override
	 public AuthenticationManager authenticationManagerBean() throws Exception {
	     return super.authenticationManagerBean();
	 }
	 
	 @Autowired
	 private LoginSuccessHandler loginSuccessHandler;
}
