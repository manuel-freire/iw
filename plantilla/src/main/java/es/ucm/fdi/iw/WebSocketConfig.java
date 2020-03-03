package es.ucm.fdi.iw;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import es.ucm.fdi.iw.control.IwSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(iwWebSocket(), "/ws");
    }

    /**
     * Bean for websocket-manager retrieval.
     * 
     * Returning a WebSocketHandler would have been enough for the registry,
     * but the IwSocketHandler has extra methods, most notably sendText() 
     * 
     * @return the shared IwSocketHandler instance
     */
    @Bean
    public IwSocketHandler iwWebSocket() {
        return new IwSocketHandler();
    }
}