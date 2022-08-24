package sosal_network.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
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
    @Size(min = 3, message = "Никнейм не может содержать менее 3-ёх символов")
    @Size(max = 20, message = "Слишком длинный никнейм")
    private String username;

    /**
     * пароль пользователя
     **/

    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 5, message = "Пароль не может состоять менее чем из 5ти символов")
    @JsonIgnore
    private String password;

    /**
     * подтвержденный пароль пользователя
     **/
    @Transient
    @JsonIgnore
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
    @JsonIgnore
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
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();


    @OneToOne(targetEntity = Image.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;

    @OneToOne(targetEntity = BanInfo.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "baninfo_id", referencedColumnName = "id")
    private BanInfo banInfo;

    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    @OneToOne(targetEntity = ProfileInfo.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_info_id")
    @JsonIgnoreProperties(value = "user", allowSetters = true)
    private ProfileInfo profileInfo;

    /**
     * Основной конструктор
     **/
    public User(String username, String password, String passwordConfirm, String email, BanInfo banInfo) {
        this.username = username;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.userEmail = email;
        this.registrationDate = LocalDate.now();
        this.banInfo = banInfo;
    }

    public String dateFormat() {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                .withLocale(new Locale("ru"))
                .format(this.registrationDate);
    }


    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {

        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
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