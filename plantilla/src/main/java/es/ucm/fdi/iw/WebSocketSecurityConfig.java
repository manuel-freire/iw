package es.ucm.fdi.iw;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

import es.ucm.fdi.iw.model.User;

/**
 * Similar to SecurityConfig, but for websockets that use STOMP.
 * 
 * @see https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html
 */
@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {
    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        // messages.simpDestMatchers("/user/**").authenticated();
        messages.simpSubscribeDestMatchers("/topic/admin")	// only admins can subscribe
                    .hasRole(User.Role.ADMIN.toString())
                .anyMessage().authenticated(); 				// must log in to use websockets
        return messages.build();
    }
}