package sosal_network.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sosal_network.entity.ProfileInfo;
import sosal_network.entity.User;

import java.util.List;

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

    //ProfileInfo findProfileInfoByUserAndNameLikeOrSurnameLike(User user, String search);

    /**
     * Метод findProfileInfosByUsers для поиска профилей друзей
     **/
    @Query(value = "SELECT * FROM jpa.profile_info WHERE user_id in (?1)", nativeQuery = true)
    Page<ProfileInfo> findProfileInfosByUsers(List<User> users, Pageable pageable);

    /**
     * Метод findProfileInfosByUsersWithSearch для поиска профилей друзей с определенными именем и фамилией
     **/
    @Query(value = "SELECT * FROM jpa.profile_info WHERE user_id in (?1) AND CONCAT(surname, ' ', name) LIKE CONCAT('%', ?2,'%')", nativeQuery = true)
    Page<ProfileInfo> findProfileInfosByUsersWithSearch(List<User> users, String searchLine, Pageable pageable);


    /**
     * Метод findProfileInfosByUsers для поиска профилей незнакомцев
     **/
    @Query(value = "SELECT * FROM jpa.profile_info WHERE user_id not in (?1)", nativeQuery = true)
    List<ProfileInfo> findStrangerProfileInfosByUsers(List<User> users, Pageable pageable);

    /**
     * Метод findProfileInfosByUsersWithSearch для поиска профилей незнакомцев с определенными именем и фамилией
     **/
    @Query(value = "SELECT * FROM jpa.profile_info WHERE user_id not in (?1) AND CONCAT(surname, ' ', name) LIKE CONCAT('%', ?2,'%')", nativeQuery = true)
    List<ProfileInfo> findStrangerProfileInfosByUsersWithSearch(List<User> users, String searchLine, Pageable pageable);
}
