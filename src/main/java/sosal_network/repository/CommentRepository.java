package sosal_network.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sosal_network.entity.Comment;
import sosal_network.entity.Post;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findCommentsByPostOrderByTime(Post post, Pageable pageable);
}
