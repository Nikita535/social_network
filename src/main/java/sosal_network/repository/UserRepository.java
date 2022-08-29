package sosal_network.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sosal_network.entity.User;

import java.util.List;

/**
 * Class UserRepository - класс для основных действий с БД
 **/
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Метод findByUsername для поиска пользователя по имени
     * param username - имя пользователя
     * author - Nikita
     **/
    User findByUsername(String username);

    /**
     * Метод findByUserEmail для поиска пользователя по почте
     * param email - почта пользователя
     * author - Nekit
     **/
    User findByUserEmail(String email);

    User findUserById(Long id);


    @Query(value = "select * from jpa.users where id in (select first_user_id from jpa.friends where (second_user_id = ?1 and invite_status ='ACCEPTED' ) union select second_user_id from jpa.friends where (first_user_id = ?1 and invite_status ='ACCEPTED'))", nativeQuery = true)
    Page<User> findFriendUsers(User user, Pageable pageable);

    @Query(value = "select * from jpa.users where id in (select first_user_id from jpa.friends where (second_user_id = ?1 and invite_status ='ACCEPTED' ) union select second_user_id from jpa.friends where (first_user_id = ?1 and invite_status ='ACCEPTED')) and profile_info_id in (select id from jpa.profile_info where CONCAT(surname, ' ', name) LIKE CONCAT('%', (?2),'%')) ", nativeQuery = true)
    Page<User> findFriendUsersWithSearch(User user, String searchLine, Pageable pageable);

    @Query(value = """
            select * from jpa.users\s
            where id not in ( select first_user_id\s
                             from jpa.friends\s
                             where (second_user_id = ?1 and (invite_status ='ACCEPTED' or invite_status ='PENDING' ) ) \s
                             union select second_user_id\s
                             from jpa.friends\s
                             where (first_user_id = ?1 and invite_status ='ACCEPTED') )
                             and ( ( username not like 'ADMIN') and ( username not like :#{#user.username}) )""", nativeQuery = true)
    List<User> findStrangers(@Param("user") User user, Pageable pageable);

    @Query(value = """
            select * from jpa.users\s
            where id not in ( select first_user_id\s
                             from jpa.friends\s
                             where (second_user_id = ?1 and (invite_status ='ACCEPTED' or invite_status ='PENDING' ) ) \s
                             union select second_user_id\s
                             from jpa.friends\s
                             where (first_user_id = ?1 and invite_status ='ACCEPTED') )
                             and ( ( username not like 'ADMIN') and ( username not like :#{#user.username}) )
                             and profile_info_id in (select id from jpa.profile_info where CONCAT(surname, ' ', name) LIKE CONCAT('%', (?2),'%'))""", nativeQuery = true)
    List<User> findStrangersWithSearch(@Param("user") User user, String searchLine, Pageable pageable);

    @Query(value = "select * from jpa.users where id in (select first_user_id from jpa.friends where (second_user_id = ?1 and invite_status ='PENDING'))", nativeQuery = true)
    List<User> fiendReceivedInvites(User user, Pageable pageable);

    @Query(value = "select * from jpa.users where id in (select first_user_id from jpa.friends where (second_user_id = ?1 and invite_status ='PENDING'))" +
            "and profile_info_id in (select id from jpa.profile_info where CONCAT(surname, ' ', name) LIKE CONCAT('%', (?2),'%'))", nativeQuery = true)
    List<User> fiendReceivedInvitesWithSearch(User user, String searchLine, Pageable pageable);


}
