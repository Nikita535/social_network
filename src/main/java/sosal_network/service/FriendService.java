package sosal_network.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import sosal_network.Enum.InviteStatus;
import sosal_network.entity.Friend;
import sosal_network.entity.ProfileInfo;
import sosal_network.entity.User;
import sosal_network.repository.FriendRepository;
import sosal_network.repository.ProfileInfoRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Class friendService - класс для основных операций над друзьями пользователя
 **/
@Service
@Slf4j
public class FriendService {

    @PersistenceContext
    private EntityManager em;
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
        return friends;
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
    public void save(Friend friend) {
        friendRepository.save(friend);
    }


    public JSONObject generateModelOfFriendList(JSONObject response, String username, String searchLine, String pageString, Integer length){
        response = findFriendProfilesByUsernameFirst(response, username, searchLine, pageString, length);
        return response;
    }


    public JSONObject findFriendProfilesByUsernameFirst(JSONObject response, String username, String searchLine, String pageString,
                                                 Integer length){
        int page = Integer.parseInt(pageString);
        int sizeOfFriends;

        List<ProfileInfo> profiles = new LinkedList<>();
        List<User> friends = new LinkedList<>(getAcceptedFriends(username) );
        List<ProfileInfo> allFriendProfiles = new LinkedList<>();
        if (Objects.equals(searchLine, "")){
            for (int i = (page - 1) * length; i < page * length && i < friends.size(); i++)
                profiles.add(userService.findByUser_Username(friends.get(i).getUsername()));
            response.append("friendProfiles", profiles);
            sizeOfFriends = friends.size();

            for (User friend : friends)
                allFriendProfiles.add(userService.findByUser_Username(friend.getUsername()));
        }else {
            Pattern pattern = Pattern.compile(searchLine);

            List<ProfileInfo> newProfiles = new LinkedList<>();
            for (User friend: friends)
                profiles.add(userService.findByUser_Username(friend.getUsername()));

            for (ProfileInfo profile: profiles){
                if (pattern.matcher( profile.getSurname() + " " + profile.getName()).find())
                    newProfiles.add(profile);
            }
            sizeOfFriends = newProfiles.size();
            allFriendProfiles = newProfiles;

            profiles = new LinkedList<>();
            for (int i = (page - 1) * length; i < page * length && i < newProfiles.size(); i++)
                profiles.add(newProfiles.get(i));
            response.append("friendProfiles", profiles);
        }

        if (profiles.size() == 0 && sizeOfFriends == 0 && !Objects.equals(searchLine, ""))
            response.append("errorNoSuchFriends", true);

        if (page == 1)
            response.append("showFriends", true);


        response = findStrangersProfilesByUsernameSecond(response, username, searchLine, page,allFriendProfiles,
                length - profiles.size(), length, sizeOfFriends);

        return response;
    }


    @Transactional
    public List<ProfileInfo> allProfileInfos(String similarTo) {
        return em.createQuery("SELECT u FROM ProfileInfo u WHERE CONCAT(u.surname, ' ', u.name) LIKE CONCAT('%',:similarTo,'%')", ProfileInfo.class)
                .setParameter("similarTo", similarTo).getResultList();
    }

    public JSONObject findStrangersProfilesByUsernameSecond(JSONObject response, String username, String searchLine,Integer page,
                                                       List<ProfileInfo> profilesOfFriends,Integer size,
                                                       Integer length, Integer sizeOfFriends){
        int sizeOfStrangers;
        List<ProfileInfo> profiles;

        List<ProfileInfo> profilesNew = new LinkedList<>();
        List<ProfileInfo> profilesReceived = new LinkedList<>();
        profiles = allProfileInfos(searchLine);
        profilesOfFriends.add(profileInfoRepository.findByUser_Username(username));

        for (ProfileInfo profile: profiles)
            if (!profilesOfFriends.contains(profile))
                profilesNew.add(profile);
        profiles = new LinkedList<>();
        sizeOfStrangers = profilesNew.size();

        if (Objects.equals(searchLine, "")){
            for (int i = page == 1 ? 0 : ((page - 1) * length - profilesOfFriends.size()); i < page * length && i < profilesNew.size() && profiles.size() < size && i > -1; i++) {
                if (!isInviteRecieved(profilesNew.get(i).getUser().getUsername()))
                    profiles.add(profilesNew.get(i));
                else
                    profilesReceived.add(profilesNew.get(i));
            }

            response.append("profilesOfStrangers", profiles);

            response.append("profilesOfReceivedStrangers", profilesReceived);

        }else {
            for (int i = page == 1 ? 0 : (page * length - profilesOfFriends.size()); i < page * length && i < profilesNew.size() && profiles.size() < size && i > -1; i++)
                if (!isInviteRecieved(username))
                    profiles.add(profilesNew.get(i));
                else
                    profilesReceived.add(profilesNew.get(i));

            response.append("profilesOfStrangers", profiles);
            response.append("profilesOfReceivedStrangers", profilesReceived);
        }

        if (profiles.size() == 0 && sizeOfStrangers == 0 && !Objects.equals(searchLine, ""))
            response.append("errorNoSuchStrangers", true);

        if (profiles.size() != 0 && page * length - profilesOfFriends.size() <= length)
            response.append("ShowStrangers", true);

        response = checkIsFriendArray(response, profiles);

        response.append("pages", Math.ceil(((float)sizeOfFriends + (float)sizeOfStrangers) / length));
        return response;
    }

    public JSONObject checkIsFriendArray(JSONObject response, List<ProfileInfo> profiles){
        List<Boolean> profilesChecked = new LinkedList<>();
        for (ProfileInfo profile : profiles) {
            profilesChecked.add(isInviteSend(profile.getUser().getUsername()));
        }
        response.append("profilesChecked", profilesChecked);
        return response;
    }


    public String clearSearchLine(String searchLine){
        return searchLine.replaceAll("[^A-Za-zА-Яа-я0-9 ]", "");
    }
}
