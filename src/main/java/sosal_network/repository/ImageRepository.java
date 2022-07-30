package sosal_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sosal_network.entity.Image;
import sosal_network.entity.User;

import javax.persistence.Lob;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {
    Image findImageByUser(User user);
    Image findImageByUser_Username(String user_username);
}
