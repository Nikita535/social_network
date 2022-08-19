package sosal_network.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import sosal_network.entity.ChatMessage;
import sosal_network.entity.ProfileInfo;
import sosal_network.entity.User;
import sosal_network.repository.FriendRepository;
import sosal_network.repository.MessageRepository;
import sosal_network.repository.ProfileInfoRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChatMessageService {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    FriendRepository friendRepository;

    @Autowired
    ProfileInfoRepository profileInfoRepository;

    public List<ChatMessage> showAllMessages(User userFrom, User userTo) {
        return Stream.concat(messageRepository.findChatMessagesByUserFromAndUserTo(userFrom, userTo).stream(),
                        messageRepository.findChatMessagesByUserFromAndUserTo(userTo, userFrom).stream()).
                sorted(Comparator.comparing(ChatMessage::getTime)).toList();
    }

    public ChatMessage showLastMessage(User userFrom, User userTo) {
        List<ChatMessage> aLLMessages = showAllMessages(userFrom, userTo);
        return aLLMessages.size() != 0 ? aLLMessages.get(aLLMessages.size() - 1): null;
    }

    public List<User> findFriend(User user, int page){
        return friendRepository.findFriendByFirstUserOrSecondUser(user, PageRequest.of( page, 8)).stream().
                map(friend -> Objects.equals(friend.getFirstUser().getId(), user.getId()) ?
                        friend.getSecondUser(): friend.getFirstUser()).collect(Collectors.toList());
    }
    public List<ProfileInfo> getChatFriends(User user, int page){
        return findFriend(user, page).stream().map(messageUser -> profileInfoRepository.findProfileInfoByUser(messageUser)).collect(Collectors.toList());
    }

//    public List<JSONObject> messagesToJSON(User user, User friend) {
//        List<ChatMessage> allMessages = showAllMessages(user, friend);
//        return allMessages.stream().map(message->
//        {
//            JSONObject jsonObject=new JSONObject();
//            jsonObject.append("userFrom",message.getUserFrom().getUsername());
//            jsonObject.append("userTo",message.getUserTo().getUsername());
//            jsonObject.append("content",message.getContent());
//            jsonObject.append("time",message.getTime());
//            return jsonObject;
//        }).collect(Collectors.toList());
//    }
}
