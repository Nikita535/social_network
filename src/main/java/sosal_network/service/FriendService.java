package sosal_network.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sosal_network.entity.Friend;
import sosal_network.entity.ProfileInfo;
import sosal_network.entity.User;
import sosal_network.repository.FriendRepository;
import sosal_network.repository.ProfileInfoRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
        Friend friend = new Friend(friendUser, user);
        user.getFriendsList().add(friend);
        saveFriend(friend);
        userService.resaveUser(user);
    }

    public void Unfriend(User user, User friendUser) {
        Friend friendToUnfriend = findFriendByFriendUserAndUserID(friendUser, user);
        user.getFriendsList().remove(friendToUnfriend);
        deleteFriendByFriendUserAndUserID(friendToUnfriend);
        userService.resaveUser(user);
    }

    @Transactional
    public boolean isFriend(String username) {
        User userFromSession = userService.getUserAuth();
        for (Friend friend : userFromSession.getFriendsList()) {
            if (friend.getFriendUser().getUsername().equals(username)) {
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


    public Object[] findFriendProfilesByUsername(String username, String searchLine, String pageString,
                                                 Integer lenght){
        int page = Integer.parseInt(pageString);
        Integer sizeOfFriends;

        List<ProfileInfo> profiles = new LinkedList<>();
        List<Friend> friends = new LinkedList<>(userService.findUserByUsername(username).getFriendsList());
        List<ProfileInfo> allFriendProfiles = new LinkedList<>();
        if (Objects.equals(searchLine, "")){
            for (int i = (page - 1) * lenght; i < page * lenght && i < friends.size(); i++)
                profiles.add(userService.findByUser_Username(friends.get(i).getFriendUser().getUsername()));
            sizeOfFriends = friends.size();

            for (int i = 0; i < friends.size(); i++)
                allFriendProfiles.add(userService.findByUser_Username(friends.get(i).getFriendUser().getUsername()));
        }else {
            Pattern pattern = Pattern.compile(searchLine);

            List<ProfileInfo> newProfiles = new LinkedList<>();
            for (Friend friend: friends)
                profiles.add(userService.findByUser_Username(friend.getFriendUser().getUsername()));

            for (ProfileInfo profile: profiles){
                if (pattern.matcher( profile.getSurname() + " " + profile.getName()).find())
                    newProfiles.add(profile);
            }
            sizeOfFriends = newProfiles.size();
            allFriendProfiles = newProfiles;

            profiles = new LinkedList<>();
            for (int i = (page - 1) * lenght; i < page * lenght && i < newProfiles.size(); i++)
                profiles.add(newProfiles.get(i));
        }


        return new Object[]{profiles, sizeOfFriends, allFriendProfiles};
    }

    public List<ProfileInfo> allProfileInfos(String similarTo) {
        return em.createQuery("SELECT u FROM ProfileInfo u WHERE CONCAT(u.surname, ' ', u.name) LIKE CONCAT('%',:similarTo,'%')", ProfileInfo.class)
                .setParameter("similarTo", similarTo).getResultList();
    }

    public Object[] findStrangersProfilesByUsername(String username, String searchLine,
                                                             String pageString, List<ProfileInfo> profilesOfFriends,
                                                             Integer size,
                                                             Integer lenght){
        int page = Integer.parseInt(pageString);
        Integer sizeOfStrangers;
        List<ProfileInfo> profiles = new LinkedList<>();

        if (Objects.equals(searchLine, "")){
            List<ProfileInfo> profilesNew = new LinkedList<>();
            profiles = allProfileInfos(searchLine);
            profilesOfFriends.add(profileInfoRepository.findByUser_Username(username));

            for (ProfileInfo profile: profiles)
                if (!profilesOfFriends.contains(profile))
                        profilesNew.add(profile);
            profiles = new LinkedList<>();
            sizeOfStrangers = profilesNew.size();


            for (int i = page == 1 ? 0 : (page * lenght - profilesOfFriends.size() - 1); i < page * lenght && i < profilesNew.size() && profiles.size() < size; i++)
                profiles.add(profilesNew.get(i));


        }else {
            List<ProfileInfo> profilesNew = new LinkedList<>();
            profiles = allProfileInfos(searchLine);
            profilesOfFriends.add(profileInfoRepository.findByUser_Username(username));

            for (ProfileInfo profile: profiles)
                if (!profilesOfFriends.contains(profile))
                    profilesNew.add(profile);
            profiles = new LinkedList<>();

            sizeOfStrangers = profilesNew.size();

            for (int i = page == 1 ? 0 : (page * lenght - profilesOfFriends.size()); i < page * lenght && i < profilesNew.size() && profiles.size() < size; i++)
                profiles.add(profilesNew.get(i));

        }

        return new Object[]{profiles, sizeOfStrangers};
    }

}

