package sosal_network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sosal_network.entity.ChatMessage;
import sosal_network.entity.User;
import sosal_network.repository.FriendRepository;
import sosal_network.repository.MessageRepository;
import sosal_network.repository.ProfileInfoRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class ChatMessageService {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    FriendRepository friendRepository;

    @Autowired
    ProfileInfoRepository profileInfoRepository;

    private static final int MESSAGE_PAGE_SIZE=10;

    public List<ChatMessage> showAllMessages(User userFrom, User userTo,int page) {
        return messageRepository.findChatMessagesByUserFromAndUserTo(userFrom,userTo,PageRequest.of(page,MESSAGE_PAGE_SIZE));
    }

    public ChatMessage showLastMessage(User userFrom, User userTo) {
        List<ChatMessage> allMessages = showAllMessages(userFrom, userTo,0);
        return allMessages.size() != 0 ? allMessages.get(0) : null;
    }

    public List<User> getChatFriends(User user, int page) {
        return friendRepository.findFriendByFirstUserOrSecondUser(user, PageRequest.of(page, 8)).stream().
                map(friend -> Objects.equals(friend.getFirstUser(), user) ?
                        friend.getSecondUser() : friend.getFirstUser())
                .sorted(Comparator.comparing(friend -> showLastMessage(user, (User) friend) != null ?
                showLastMessage(user, (User) friend).getTime() : LocalDateTime.MIN).reversed()).toList();
    }

}
