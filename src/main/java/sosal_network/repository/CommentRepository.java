package sosal_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sosal_network.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

}
