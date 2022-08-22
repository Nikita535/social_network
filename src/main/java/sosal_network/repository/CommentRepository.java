package sosal_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sosal_network.entity.Comment;
import sosal_network.entity.Post;

import java.util.List;

public interface CommentRepository  extends JpaRepository<Comment, Long> {

    List<Comment> findCommentByPost(Post post);
}
