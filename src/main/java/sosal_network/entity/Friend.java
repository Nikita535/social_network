package sosal_network.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sosal_network.Enum.InviteStatus;

import javax.persistence.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    //    /**
//     * Друг пользователя
//     **/
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "friend_id")
//    private User friendUser;
//
//    /**
//     * Пользователь,у которого в друзьях friendUser
//     **/
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "user_id")
//    private User userID;
//
//    public Friend(User friendUser, User userID) {
//        this.friendUser = friendUser;
//        this.userID = userID;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Friend friend = (Friend) o;
//        return Objects.equals(friendUser, friend.friendUser) && Objects.equals(userID, friend.userID);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(friendUser, userID);
//    }
    @OneToOne(cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
    @JoinColumn(name = "first_user_id", referencedColumnName = "id")
    private User firstUser;

    @OneToOne(cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
    @JoinColumn(name = "second_user_id", referencedColumnName = "id")
    private User secondUser;

    @Enumerated(EnumType.STRING)
    private InviteStatus inviteStatus;

    public Friend(User firstUser, User secondUser, InviteStatus inviteStatus) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.inviteStatus = inviteStatus;
    }
}
