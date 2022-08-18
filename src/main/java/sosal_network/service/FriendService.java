package sosal_network.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import sosal_network.Enum.InviteStatus;
import sosal_network.entity.Friend;
import sosal_network.entity.Image;
import sosal_network.entity.ProfileInfo;
import sosal_network.entity.User;
import sosal_network.repository.FriendRepository;
import sosal_network.repository.ImageRepository;
import sosal_network.repository.ProfileInfoRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.swing.text.StyledEditorKit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    @Autowired
    private ProfileInfoRepository profileInfoRepository;


    public String redirectToFriendListOrToProfile(String username, String where){
        if (where != null)
            return "redirect:/" + where + "/friendList/1";
        return "redirect:/user/" + username;
    }

    @Transactional
    public String sendInvite(String username, String where) {
        User userFromSession = userService.getUserAuth();
        User friendUser = userService.findUserByUsername(username);
        if (friendUser == null) {
            log.error("no such user");
            return redirectToFriendListOrToProfile(username, where);
        }
        if (existsByFirstUserAndSecondUser(userFromSession, friendUser) || existsByFirstUserAndSecondUser(friendUser, userFromSession)) {
            log.error("user is already in friend list");
            return redirectToFriendListOrToProfile(username, where);
        }
        Friend friend = new Friend(userFromSession, friendUser, InviteStatus.PENDING);
        save(friend);
        return redirectToFriendListOrToProfile(username, where);
    }

    @Transactional
    public String resultInvite(String username, int result, String where) {
        User userFromSession = userService.getUserAuth();
        User friendUser = userService.findUserByUsername(username);
        if (friendUser == null) {
            log.error("no such user");
            return redirectToFriendListOrToProfile(username, where);
        }
        if (!existsByFirstUserAndSecondUser(userFromSession, friendUser) && !existsByFirstUserAndSecondUser(friendUser, userFromSession)) {
            log.error("users are not friends");
            return redirectToFriendListOrToProfile(username, where);
        }
        Friend friend = findLinkedFriends(userFromSession, friendUser);

        switch (result) {
            case 1: {
                friend.setInviteStatus(InviteStatus.ACCEPTED);
                save(friend);
                break;
            }
            case 2: {
                friendRepository.delete(friend);
                break;
            }
            default:
                break;
        }
        return redirectToFriendListOrToProfile(username, where);
    }

    @Transactional
    public String deleteFriend(String username, String where) {

        User userFromSession = userService.getUserAuth();
        User friendUser = userService.findUserByUsername(username);
        if (friendUser == null) {
            log.error("no such user");
            return redirectToFriendListOrToProfile(username, where);
        }
        if (!existsByFirstUserAndSecondUser(userFromSession, friendUser) && !existsByFirstUserAndSecondUser(friendUser, userFromSession)) {
            log.error("users are not friends");
            return redirectToFriendListOrToProfile(username, where);
        }
        if (existsByFirstUserAndSecondUser(userFromSession, friendUser)) {
            deleteFriendByFirstUserAndSecondUser(userFromSession, friendUser);
        } else {
            deleteFriendByFirstUserAndSecondUser(friendUser, userFromSession);
        }
        return redirectToFriendListOrToProfile(username, where);
    }

    @Transactional
    public Friend findLinkedFriends(User firstUser, User secondUser) {
        if (existsByFirstUserAndSecondUser(firstUser, secondUser)) {
            return findFriendByFirstUserAndSecondUser(firstUser, secondUser);
        } else {
            return findFriendByFirstUserAndSecondUser(secondUser, firstUser);
        }
    }

    public List<User> getFriends(String username) {
        User userFromSession = userService.findUserByUsername(username);
        List<Friend> friendsByFirstUser = findFriendsByFirstUser(userFromSession);
        List<Friend> friendsBySecondUser = findFriendsBySecondUser(userFromSession);
        List<User> friends = new ArrayList<>();
        for (Friend friend : friendsByFirstUser) {
            friends.add(userService.findUserByUsername(friend.getSecondUser().getUsername()));
        }
        for (Friend friend : friendsBySecondUser) {
            friends.add(userService.findUserByUsername(friend.getFirstUser().getUsername()));
        }
        return friends;
    }
    public List<User> getAcceptedFriends(String username) {
        User userFromSession = userService.findUserByUsername(username);
        List<Friend> friendsByFirstUser = findFriendsByFirstUser(userFromSession).stream().filter(x->x.getInviteStatus()==InviteStatus.ACCEPTED).toList();
        List<Friend> friendsBySecondUser = findFriendsBySecondUser(userFromSession).stream().filter(x->x.getInviteStatus()==InviteStatus.ACCEPTED).toList();
        List<User> friends = new ArrayList<>();
        for (Friend friend : friendsByFirstUser) {
            friends.add(userService.findUserByUsername(friend.getSecondUser().getUsername()));
        }
        for (Friend friend : friendsBySecondUser) {
            friends.add(userService.findUserByUsername(friend.getFirstUser().getUsername()));
        }
        return friends.stream().sorted(Comparator.comparing(User::getId)).collect(Collectors.toList());
    }

    public List<User> getReceivedInvitesUsers(String username){
        User userFromSession = userService.findUserByUsername(username);
        return findFriendsBySecondUser(userFromSession).stream().
                filter(x->x.getInviteStatus()==InviteStatus.PENDING).map(Friend::getFirstUser).toList();
    }

    public List<ProfileInfo> getReceivedInvitesProfiles(String username){
        User userFromSession = userService.findUserByUsername(username);
        return findFriendsBySecondUser(userFromSession).stream().
                filter(x->x.getInviteStatus()==InviteStatus.PENDING).map(i->profileInfoRepository.findProfileInfoByUser(i.getFirstUser())).toList();
    }

    @Transactional
    public boolean isFriends(String username) {
        User user = userService.getUserAuth();
        List<User> friends = getFriends(userService.getUserAuth().getUsername());
        for (User friendUser : friends) {
            if (friendUser.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean checkFriendStatus(String username) {
        User userFromSession = userService.getUserAuth();
        User friendUser = userService.findUserByUsername(username);
        Friend friend = findLinkedFriends(userFromSession, friendUser);
        if (friend == null) {
            return false;
        }
        return isFriends(username) && friend.getInviteStatus() == InviteStatus.ACCEPTED;
    }

    @Transactional
    public boolean isInviteRecieved(String username) {
        User userFromSession = userService.getUserAuth();
        User friendUser = userService.findUserByUsername(username);
        Friend friend = findLinkedFriends(userFromSession, friendUser);
        if (friend == null) {
            return false;
        }
        return friend.getSecondUser().equals(userFromSession) && friend.getInviteStatus() == InviteStatus.PENDING;
    }

    @Transactional
    public boolean isInviteSend(String username) {
        User userFromSession = userService.getUserAuth();
        User friendUser = userService.findUserByUsername(username);
        Friend friend = findLinkedFriends(userFromSession, friendUser);
        if (friend == null) {
            return false;
        }
        return friend.getFirstUser().equals(userFromSession) && friend.getInviteStatus() == InviteStatus.PENDING;
    }

    @Transactional
    public boolean existsByFirstUserAndSecondUser(User firstUser, User secondUser) {
        return friendRepository.existsByFirstUserAndSecondUser(firstUser, secondUser);
    }

    @Transactional
    public Friend findFriendByFirstUserAndSecondUser(User firstUser, User secondUser) {
        return friendRepository.findFriendByFirstUserAndSecondUser(firstUser, secondUser);
    }

    @Transactional
    public void deleteFriendByFirstUserAndSecondUser(User firstUser, User secondUser) {
        friendRepository.deleteFriendByFirstUserAndSecondUser(firstUser, secondUser);
    }

    @Transactional
    public List<Friend> findFriendsByFirstUser(User firstUser) {
        return friendRepository.findFriendsByFirstUser(firstUser);
    }

    @Transactional
    public List<Friend> findFriendsBySecondUser(User secondUser) {
        return friendRepository.findFriendsBySecondUser(secondUser);
    }

    @Transactional
    public List<ProfileInfo> findFriendsBySecondUserAndInviteStatus(User user, int page)
    {
        return friendRepository.findFriendsBySecondUserAndInviteStatus(user,
                InviteStatus.PENDING, PageRequest.of( page, 5)).stream().map(friend ->
                profileInfoRepository.findProfileInfoByUser(friend.getFirstUser())).collect(Collectors.toList());
    }

    @Transactional
    public List<ProfileInfo> findFriendsBySecondUserAndInviteStatusWithSearch(User user, String searchLine, int page){
        return friendRepository.findFriendsBySecondUserAndInviteStatusWithSearch(user, searchLine,
                InviteStatus.PENDING, PageRequest.of( page, 5)).stream().
                map(name -> profileInfoRepository.findProfileInfoByUser(userService.findUserById(name))).collect(Collectors.toList());
    }


    @Transactional
    public void save(Friend friend) {
        friendRepository.save(friend);
    }

    public List<Boolean> getInviteSendFriends(List<ProfileInfo> friends){
        return friends.stream().map(friend -> isInviteSend(friend.getUser().getUsername())).collect(Collectors.toList());
    }

    @Transactional
    public List<Object> findFriendsAndStrangers(String username, String searchLine, int page){

        User currentUser = userService.findUserByUsername(username);
        List<Object> response = new ArrayList<>();
        List<User> friends = new LinkedList<>(getAcceptedFriends(username));

        List<Boolean> isInviteSendStrangers = new ArrayList<>();
        Page<ProfileInfo> profilesOfFriends;
        List<ProfileInfo> profilesOfStrangers = new ArrayList<>();
        List<User> profilesReceived = getReceivedInvitesUsers(username);


        if (Objects.equals(searchLine, "")){
            profilesOfFriends = userService.findProfileInfosByUsers(friends, page);
            friends.addAll(profilesReceived);
            friends.add(currentUser);
            if (profilesOfFriends.isLast()) {
                profilesOfStrangers = userService.findStrangerProfileInfosByUsers(friends,page - profilesOfFriends.getTotalPages() + 1);
                isInviteSendStrangers = getInviteSendFriends(profilesOfStrangers.stream().toList());
            }
        }else {
            profilesOfFriends = userService.findProfileInfosByUsersWithSearch(friends, searchLine, page);
            friends.addAll(profilesReceived);
            friends.add(currentUser);

            if (profilesOfFriends.isLast()) {
                profilesOfStrangers = userService.findStrangerProfileInfosByUsersWithSearch(friends, searchLine, page - profilesOfFriends.getTotalPages() + 1);
                isInviteSendStrangers = getInviteSendFriends(profilesOfStrangers.stream().toList());
            }
        }

        response.add(profilesOfFriends.stream().toList());
        response.add(profilesOfStrangers);
        response.add(isInviteSendStrangers);
        response.add(profilesReceived);
        return response;
    }

    public List<Object> findSuggestions(String username, String searchLine, int page){
        List<Object> response = new ArrayList<>();
        List<ProfileInfo> profilesReceived;
        if (Objects.equals(searchLine, ""))
            profilesReceived = findFriendsBySecondUserAndInviteStatus(userService.findUserByUsername(username), page);
        else
            profilesReceived = findFriendsBySecondUserAndInviteStatusWithSearch(userService.findUserByUsername(username), searchLine, page);
        response.add(profilesReceived);
        return response;
    }

    public String clearSearchLine(String searchLine){
        return searchLine.replaceAll("[^A-Za-zА-Яа-я0-9 ]", "");
    }
}
