package sosal_network.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne( targetEntity = Post.class,fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToOne(targetEntity = Image.class,fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id",referencedColumnName = "id")
    private Image image;

    public PostImage(Post post, Image image) {
        this.post = post;
        this.image = image;
    }
}
