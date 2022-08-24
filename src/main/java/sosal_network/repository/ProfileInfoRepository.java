package sosal_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sosal_network.entity.ProfileInfo;

/**
 * Class ProfileInfoRepository - класс для взаимодействия с БД информации о пользователе
 **/
@Repository
public interface ProfileInfoRepository extends JpaRepository<ProfileInfo, Long> {
}
