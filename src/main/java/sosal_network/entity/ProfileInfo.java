package sosal_network.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Class ProfileInfo - класс информации о пользователе
 **/
@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProfileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Соединение с пользователем в БД
     **/
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    /**
     * имя пользователя
     **/
    private String name;
    /**
     * фамилия пользователя
     **/
    private String surname;
    /**
     * город пользователя
     **/
    private String city;
    /**
     * описание пользователя
     **/
    private String description;
    /**
     * сайт пользователя
     **/
    private String website;

    public ProfileInfo(String name, String surname, String city, String description, String website) {
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.description = description;
        this.website = website;
    }
}
