package sosal_network.controller;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sosal_network.entity.ChatMessage;
import sosal_network.entity.Post;
import sosal_network.entity.User;
import sosal_network.exception.UserWasBanedException;
import sosal_network.repository.BanRepository;
import sosal_network.repository.MessageRepository;
import sosal_network.repository.UserRepository;
import sosal_network.service.ChatMessageService;
import sosal_network.service.FriendService;
import sosal_network.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpUserRegistry userRegistry;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    BanRepository banRepository;
    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;



    @GetMapping("/messages")
    public String getChat(Model model, @AuthenticationPrincipal User authenticatedUser) {

        if (banRepository.findBanInfoById(authenticatedUser.getBanInfo().getId()).isBanStatus()) {
            throw new UserWasBanedException();
        }

        model.addAttribute("user", userService.findUserByUsername(authenticatedUser.getUsername()));
        model.addAttribute("friends", friendService.getAcceptedFriends(authenticatedUser.getUsername()));
        model.addAttribute("userService", userService);
        return "messages";
    }

    @RequestMapping(value = "/chat/{username}/{page}", method = RequestMethod.GET)
    @ResponseBody
    public List<Object> getChatRoom(@PathVariable String username,@PathVariable int page, @AuthenticationPrincipal User authenticatedUser) {
        User friend = userRepository.findByUsername(username);

        List<Object> response = new ArrayList<>();

        response.add(friend);
        response.add(chatMessageService.showAllMessages(authenticatedUser, friend,page));
        return response;
    }


    @MessageMapping("/chat.send/{id1}/{id2}")
    @SendTo("/topic/{id1}/{id2}")
    public ChatMessage sendMessage(@Payload final ChatMessage chatMessage) {
        messageRepository.save(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/push.send/{username}")
    @SendTo("/topic/push/{username}")
    public ChatMessage pushMessage(@Payload final ChatMessage chatMessage) {
        return chatMessage;
    }


    @RequestMapping(value = "/isUserInChat/{userToId}", method = RequestMethod.POST)
    @ResponseBody
    public boolean userInChatDetection(@PathVariable long userToId){

        boolean isConnected = false;
        long userFromId = userService.getUserAuth().getId();
        String userTo = userService.findUserById(userToId).getUsername();
        String topic;
        if (userFromId > userToId)
            topic = "/topic/" + userToId+ "/" + userFromId + "}";
        else
            topic = "/topic/" + userFromId + "/" + userToId + "}";
        if (userRegistry.getUser(userTo) != null) {
            isConnected = userRegistry.getUser(userTo).getSessions().toString().contains(topic);
        }

        return isConnected;
    }
    @RequestMapping(value ="/reloadMessageFriends/{page}", method = RequestMethod.GET)
    @ResponseBody
    public List<Object> showFriendsMessages(@PathVariable int page, @AuthenticationPrincipal User authenticatedUser) {
        List<Object> allInfo = new ArrayList<>();
        List<User> chatFriends = chatMessageService.getChatFriends(authenticatedUser, page);
        List<ChatMessage> lastMessages = chatFriends.stream().map(friend -> chatMessageService.showLastMessage(friend, authenticatedUser)).toList();
        allInfo.add(chatFriends);
        allInfo.add(lastMessages);
        return allInfo;
    }

    @RequestMapping(value ="/deleteMessages", method = RequestMethod.DELETE)
    @ResponseBody
    public List<Long> deleteMapping(@RequestParam(name = "deleteMessages") List<Long> selectedMessagesIds, @AuthenticationPrincipal User user)
    {
        List<Long> deletedMessages = chatMessageService.deleteMessagesByIds(selectedMessagesIds, user);
        return deletedMessages;
    }

}
