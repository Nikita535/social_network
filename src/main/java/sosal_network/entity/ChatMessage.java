package sosal_network.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User userFrom;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User userTo;

    private String content;

    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private LocalDateTime time;
}
