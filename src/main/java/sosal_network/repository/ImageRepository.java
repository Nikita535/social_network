package sosal_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sosal_network.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Image findImageById(Long id);

}
