package sosal_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sosal_network.entity.Image;
import sosal_network.entity.User;

import javax.persistence.Lob;
import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Image findImageById(Long id);

}
