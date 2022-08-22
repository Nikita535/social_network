package sosal_network.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne( targetEntity = Post.class,fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User user;

    private String content;

    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    private LocalDateTime time;
}
