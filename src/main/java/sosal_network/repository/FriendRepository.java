package sosal_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sosal_network.entity.Friend;
import sosal_network.entity.User;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Integer> {
    boolean existsByFirstUserAndSecondUser(User firstUser, User secondUser);

    Friend findFriendByFirstUserAndSecondUser(User firstUser, User secondUser);

    void deleteFriendByFirstUserAndSecondUser(User firstUser, User secondUser);

    List<Friend> findFriendsByFirstUser(User firstUser);


    List<Friend> findFriendsBySecondUser(User secondUser);


}
