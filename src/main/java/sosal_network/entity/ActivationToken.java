package sosal_network.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ActivationToken {
    /**
     * поле для времени
     **/
    private static final int EXPIRATION = 24 * 60 * 60 * 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * поле токена
     **/
    private String token;

    /**
     * Соединение с пользователем в БД
     **/
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    /**
     * Поле для даты
     **/
    private Date expiryDate;

    /**
     * Основной конструктор
     * author - Nikita
     **/
    public ActivationToken(String token, User user, Date expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

    /**
     * Метод сравнения дат
     * author - Nikita
     **/
    public boolean compareDate() {
        return (int) ((new Date().getTime() - expiryDate.getTime()) / EXPIRATION) < 2;
    }
}
