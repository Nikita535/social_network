package sosal_network.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "post")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime dateOfCreate;
    private String full_text;


    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;


    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, orphanRemoval = true)
    List<PostImage> images = new ArrayList<>();


    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnoreProperties(value = {"post"}, allowSetters = true)
    List<Comment> comments = new ArrayList<>();

    @Transient
    private String fromNow;

    public Post(LocalDateTime dateOfCreate, String full_text, User user) {
        this.dateOfCreate = dateOfCreate;
        this.full_text = full_text;
        this.user = user;
    }
}
