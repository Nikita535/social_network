package sosal_network.controller;


import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import sosal_network.Enum.InviteStatus;
import sosal_network.entity.*;
import sosal_network.repository.MessageRepository;
import sosal_network.repository.UserRepository;
import sosal_network.service.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CommentController {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @MessageMapping("/comment.send/{username}/{idPost}")
    @SendTo("/topic/post/{username}")
    public Comment sendMessage(@Payload final Comment comment, @DestinationVariable Long idPost) {
        comment.setPost(postService.findPostById(idPost));
        commentService.save(comment);
        return comment;
    }

    //@MessageMapping("/chat.newUser")
    //@SendTo("/topic/1")
    //public ChatMessage newUser(@Payload final ChatMessage chatMessage,
    //                           SimpMessageHeaderAccessor headerAccessor){
    //
    //    headerAccessor.getSessionAttributes().put("username", chatMessage.getUserFrom().getUsername());
    //    return chatMessage;
    //}
}
