package sosal_network.entity;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sosal_network.Enum.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;


/**
 * Class User - класс сущности пользователя
 **/
@Entity(name = "user")
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    /**
     * имя пользователя
     **/
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 3,message = "Никнейм не может содержать менее 3-ёх символов")
    @Size(max = 20,message = "Слишком длинный никнейм")
    private String username;
//    /** фамилия пользователя  **/
//    private String userSurname;

    /**
     * пароль пользователя
     **/
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 5,message = "Пароль не может состоять менее чем из 5ти символов")
    private String password;

    /**
     * подтвержденный пароль пользователя
     **/
    @Transient
//    @NotBlank(message = "Поле не может быть пустым")
    private String passwordConfirm;

    /**
     * почта пользователя
     **/
    @Email(message = "Поле должно иметь формат эл.почты")
    @NotBlank(message = "Поле не может быть пустым")
    private String userEmail;

    /**
     * активность пользователя
     **/
    private Boolean active;

    /**
     * дата регистрации пользователя
     **/
    private LocalDate registrationDate;

    /**
     * Соединение таблиц пользователя с ролями
     * author - Nikita
     **/
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

//    /**
//     * Соединение таблиц пользователя с друзьями
//     * author - Nikita
//     **/
//    @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
//    private Set<User> friends = new HashSet<>();


    @OneToOne(targetEntity = Image.class,cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;


    /**
     * Основной конструктор
     **/
    public User(String username, String password, String passwordConfirm, String email) {
        this.username = username;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.userEmail = email;
        this.registrationDate = LocalDate.now();
    }

    public String dateFormat() {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                .withLocale(new Locale("ru"))
                .format(this.registrationDate);
    }

//    public void addImageToUser(Image image) {
//        images.add(image);
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }


}