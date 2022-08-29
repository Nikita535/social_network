package sosal_network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sosal_network.entity.ChatMessage;
import sosal_network.entity.User;
import sosal_network.repository.FriendRepository;
import sosal_network.repository.MessageRepository;
import sosal_network.repository.ProfileInfoRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
        return aLLMessages.size() != 0 ? aLLMessages.get(aLLMessages.size() - 1) : null;
    }

    public List<User> getChatFriends(User user, int page) {
        return friendRepository.findFriendByFirstUserOrSecondUser(user, PageRequest.of(page, 8)).stream().
                map(friend -> Objects.equals(friend.getFirstUser(), user) ?
                        friend.getSecondUser() : friend.getFirstUser())
                .sorted(Comparator.comparing(friend ->showLastMessage(user, (User) friend).getTime()).reversed()).toList();
    }

}
