package sosal_network.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sosal_network.entity.ChatMessage;
import sosal_network.entity.Image;
import sosal_network.entity.Post;
import sosal_network.entity.User;
import sosal_network.repository.MessageRepository;
import sosal_network.repository.UserRepository;
import sosal_network.service.*;

import java.util.*;

@Controller
public class ChatController {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private PostService postService;


    @GetMapping("/messages")
    public String getChat(Model model, @AuthenticationPrincipal User authentificatedUser){

        List<User> friends = friendService.getAcceptedFriends(authentificatedUser.getUsername());

        model.addAttribute("user", userService.findUserByUsername(authentificatedUser.getUsername()));
        model.addAttribute("profileInfo", userService.findProfileInfoByUser(authentificatedUser));
        model.addAttribute("friends",friendService.getAcceptedFriends(authentificatedUser.getUsername()));

        model.addAttribute("isFriend",friendService.isFriends(authentificatedUser.getUsername()));
        model.addAttribute("friendAccepted",friendService.checkFriendStatus(authentificatedUser.getUsername()));
        model.addAttribute("isInviteRecieved",friendService.isInviteRecieved(authentificatedUser.getUsername()));
        model.addAttribute("isInviteSend",friendService.isInviteSend(authentificatedUser.getUsername()));
        model.addAttribute("userService", userService);

        return "messages";
    }

    @GetMapping("/messages/{username}")
    public String getChat1(Model model, @PathVariable String username, @AuthenticationPrincipal User user){
        User friend = userRepository.findByUsername(username);
        model.addAttribute("userFrom", user);
        model.addAttribute("userTo", friend);
        model.addAttribute("allMessages", chatMessageService.showAllMessages(user, friend));
        return "index";
    }


    @MessageMapping("/chat.send/{id1}/{id2}")
    @SendTo("/topic/{id1}/{id2}")
    public ChatMessage sendMessage(@Payload final ChatMessage chatMessage){
        messageRepository.save(chatMessage);
        return chatMessage;
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
