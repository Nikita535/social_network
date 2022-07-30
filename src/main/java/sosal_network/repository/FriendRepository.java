package sosal_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sosal_network.entity.Friend;
import sosal_network.entity.User;

@Repository
public interface FriendRepository extends JpaRepository<Friend,Integer> {
    Friend findFriendByFriendUserAndUserID(User friendUser,User userID);
    void deleteFriendByFriendUserAndUserID(User friendUser, User userID);
}
