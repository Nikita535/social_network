package sosal_network.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Class ProfileInfo - класс информации о пользователе
 **/
@Table(name = "profile_info")
@Entity(name = "profile_info")
@Getter
@Setter
@NoArgsConstructor
public class ProfileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    /**
     * имя пользователя
     **/
    @NotBlank(message = "Поле не может быть пустым")
    @Pattern(regexp = "^([А-Я]{1}[а-яё]{1,23}|[A-Z]{1}[a-z]{1,23})$", message = "Неправильный формат имени")
    private String name;
    /**
     * фамилия пользователя
     **/
    @NotBlank(message = "Поле не может быть пустым")
    @Pattern(regexp = "^([А-Я]{1}[а-яё]{1,23}|[A-Z]{1}[a-z]{1,23})$", message = "Неправильный формат фамилии")
    private String surname;
    /**
     * город пользователя
     **/
    @NotBlank(message = "Поле не может быть пустым")
    private String city;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;
    private String description;
    /**
     * сайт пользователя
     **/
    private String website;

    public String dateFormat() {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                .withLocale(new Locale("ru"))
                .format(this.dateOfBirth);
    }

    public String toTextDate() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(this.dateOfBirth);
    }

    @JsonIgnore
    public String getAge() {
        long age = ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());
        if (age % 10 == 1 && age / 10 != 1) {
            return age + " год";
        } else {
            return age % 10 != 0 && age % 10 < 5 && age / 10 != 1 ? age + " года" : age + " лет";
        }

    }

    public ProfileInfo(String name, String surname, String city, LocalDate dateOfBirth, String description, String website) {
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.dateOfBirth = dateOfBirth;
        this.description = description;
        this.website = website;
    }
}
