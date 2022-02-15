package es.ucm.fdi.iw;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

import es.ucm.fdi.iw.model.User;

/**
 * Similar to SecurityConfig, but for websockets that use STOMP.
 * 
 * @see https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html
 */
@Configuration
public class WebSocketSecurityConfig
      extends AbstractSecurityWebSocketMessageBrokerConfigurer { 

	
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
            .simpSubscribeDestMatchers("/topic/admin")	// only admins can subscribe
            	.hasRole(User.Role.ADMIN.toString())
            .anyMessage().authenticated(); 				// must log in to use websockets
    }
}