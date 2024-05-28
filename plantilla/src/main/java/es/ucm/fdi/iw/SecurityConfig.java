package es.ucm.fdi.iw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security configuration.
 * 
 * Most security configuration will appear in this file, but according to 
 * https://spring.io/guides/topicals/spring-security-architecture/, it is not
 * a bad idea to also use method security (via @Secured annotations in methods) 
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private Environment env;

	/**
	 * Main security configuration.
	 * 
	 * The first rule that matches will be followed - so if a rule decides to grant access
	 * to a resource, a later rule cannot deny that access, and vice-versa.
	 */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> {
				String debugProperty = env.getProperty("es.ucm.fdi.debug");
				if (debugProperty != null && Boolean.parseBoolean(debugProperty.toLowerCase())) {
					// allows access to h2 console iff running under debug mode
					requests.requestMatchers("/h2/**").permitAll();
				}
                requests.requestMatchers("/css/**", "/js/**", "/img/**", "/", "/error").permitAll()
                        .requestMatchers("/api/**").permitAll()
				        .requestMatchers("/admin/**").hasRole("ADMIN")
	                    .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().authenticated();
            })
			.csrf((csrf) -> csrf
				.ignoringRequestMatchers("/api/**")
			)
            .formLogin((form) -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .permitAll()
                .successHandler(loginSuccessHandler)
            )
            .logout((logout) -> logout.permitAll());

        return http.build();
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
	 
	@Autowired
	private LoginSuccessHandler loginSuccessHandler;
}
