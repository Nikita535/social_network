package sosal_network.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Class Friend - класс сущности друзей пользователя
 * **/
@Entity(name = "friend")
@Table(name = "friends")
@Getter
@Setter
public class Friend{
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private int id;

    /** Соединение с пользователем в БД**/
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User userID;



}
