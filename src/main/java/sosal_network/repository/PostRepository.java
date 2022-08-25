package sosal_network.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sosal_network.entity.Post;
import sosal_network.entity.User;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Integer> {

    List<Post> findAllByUserOrderByIdDesc(User user, Pageable pageable);


    Post findPostById(Long id);
}




