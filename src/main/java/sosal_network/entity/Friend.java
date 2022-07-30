package sosal_network.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

/**
 * Class Friend - класс сущности друзей пользователя
 **/
@NoArgsConstructor
@Entity(name = "friend")
@Table(name = "friends")
@Getter
@Setter

/**
 * Class Friend - класс друзей пользователя
 **/
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    /**
     * Друг пользователя
     **/
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "friend_id")
    private User friendUser;

    /**
     * Пользователь,у которого в друзьях friendUser
     **/
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User userID;

    public Friend(User friendUser, User userID) {
        this.friendUser = friendUser;
        this.userID = userID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friend friend = (Friend) o;
        return Objects.equals(friendUser, friend.friendUser) && Objects.equals(userID, friend.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(friendUser, userID);
    }
}
