package sosal_network.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sosal_network.entity.Friend;
import sosal_network.entity.User;
import sosal_network.repository.FriendRepository;

/**
 * Class friendService - класс для основных операций над друзьями пользователя
 **/
@Service
@Slf4j
public class FriendService {
    @Autowired
    private UserService userService;
    @Autowired
    private FriendRepository friendRepository;

    @Transactional
    public String addFriend(String username) {
        if (isFriend(username)) {
            log.error("user is already in friend list");
            return "redirect:/user/" + username;
        }
        User userFromSession = userService.getUserAuth();
        User friendUser = userService.findUserByUsername(username);
        if (friendUser == null) {
            log.error("no such user");
            return "redirect:/user/" + username;
        }
        Friend(userFromSession, friendUser);
        Friend(friendUser, userFromSession);
        return "redirect:/user/" + username;
    }

    @Transactional
    public String deleteFriend(String username) {
        if (!isFriend(username)) {
            log.error("users are not friends");
            return "redirect:/user/" + username;
        }
        User userFromSession = userService.getUserAuth();
        User friendUser = userService.findUserByUsername(username);
        Unfriend(userFromSession, friendUser);
        Unfriend(friendUser, userFromSession);
        return "redirect:/user/" + username;
    }

    public void Friend(User user, User friendUser) {
        user.getFriends().add(friendUser);
        userService.resaveUser(user);
    }

    public void Unfriend(User user, User friendUser) {
        user.getFriends().remove(friendUser);
        userService.resaveUser(user);
    }

    @Transactional
    public boolean isFriend(String username) {
        User userFromSession = userService.getUserAuth();
        for (User user : userFromSession.getFriends()) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public Friend findFriendByFriendUserAndUserID(User friendUser, User userID) {
        return friendRepository.findFriendByFriendUserAndUserID(friendUser, userID);
    }

    @Transactional
    public void saveFriend(Friend friend) {
        friendRepository.save(friend);
    }

    @Transactional
    void deleteFriendByFriendUserAndUserID(Friend friend) {
        friendRepository.deleteFriendByFriendUserAndUserID(friend.getFriendUser(), friend.getUserID());
    }

}
