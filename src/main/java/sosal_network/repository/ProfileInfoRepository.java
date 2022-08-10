package sosal_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sosal_network.entity.ProfileInfo;
import sosal_network.entity.User;

/**
 * Class ProfileInfoRepository - класс для взаимодействия с БД информации о пользователе
 **/
@Repository
public interface ProfileInfoRepository extends JpaRepository<ProfileInfo, Integer> {
    /**
     * Метод findByUser_Username для поиска информации о пользователе по имени пользователя
     * param username - имя пользователя
     * author - Nekit
     **/
    ProfileInfo findByUser_Username(String username);

    ProfileInfo findProfileInfoByUser(User user);
}
