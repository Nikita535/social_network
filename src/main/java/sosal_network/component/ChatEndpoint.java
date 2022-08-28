package sosal_network.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.server.standard.SpringConfigurator;
import sosal_network.config.WebSocketConfiguration;
import sosal_network.repository.UserRepository;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;

@Component
//@ServerEndpoint(value="/chat-example", configurator = SpringConfigurator.class)
public class ChatEndpoint {


//    @Autowired
//    private SimpMessageSendingOperations sendingOperations;
//
//    @Autowired
//    private UserRepository userRepository;

    @EventListener
    public void handleWebSocketEventListener(final SessionConnectedEvent event){
        System.out.println("User connected");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(final SessionDisconnectEvent event){
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        final String username = (String) headerAccessor.getSessionAttributes().get("username");

        //sendingOperations.convertAndSend("/topic/online/{id1}/{id2}", username);
    }
}
