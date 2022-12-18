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


    @Query(value = "select * from public.users where id in (select first_user_id from public.friends where (second_user_id = ?1 and invite_status ='ACCEPTED' ) union select second_user_id from public.friends where (first_user_id = ?1 and invite_status ='ACCEPTED'))", nativeQuery = true)
    Page<User> findFriendUsers(User user, Pageable pageable);

    @Query(value = "select * from public.users where id in (select first_user_id from public.friends where (second_user_id = ?1 and invite_status ='ACCEPTED' ) union select second_user_id from public.friends where (first_user_id = ?1 and invite_status ='ACCEPTED')) and profile_info_id in (select id from public.profile_info where CONCAT(surname, ' ', name) LIKE CONCAT('%', (?2),'%')) ", nativeQuery = true)
    Page<User> findFriendUsersWithSearch(User user, String searchLine, Pageable pageable);

    @Query(value = """
            select * from public.users\s
            where id not in ( select first_user_id\s
                             from public.friends\s
                             where (second_user_id = ?1 and (invite_status ='ACCEPTED' or invite_status ='PENDING' ) ) \s
                             union select second_user_id\s
                             from public.friends\s
                             where (first_user_id = ?1 and invite_status ='ACCEPTED') )
                             and ( ( username not like 'ADMIN') and ( username not like :#{#user.username}) )""", nativeQuery = true)
    List<User> findStrangers(@Param("user") User user, Pageable pageable);

    @Query(value = """
            select * from public.users\s
            where id not in ( select first_user_id\s
                             from public.friends\s
                             where (second_user_id = ?1 and (invite_status ='ACCEPTED' or invite_status ='PENDING' ) ) \s
                             union select second_user_id\s
                             from public.friends\s
                             where (first_user_id = ?1 and invite_status ='ACCEPTED') )
                             and ( ( username not like 'ADMIN') and ( username not like :#{#user.username}) )
                             and profile_info_id in (select id from public.profile_info where CONCAT(surname, ' ', name) LIKE CONCAT('%', (?2),'%'))""", nativeQuery = true)
    List<User> findStrangersWithSearch(@Param("user") User user, String searchLine, Pageable pageable);

    @Query(value = "select * from public.users where id in (select first_user_id from public.friends where (second_user_id = ?1 and invite_status ='PENDING'))", nativeQuery = true)
    List<User> fiendReceivedInvites(User user, Pageable pageable);

    @Query(value = "select * from public.users where id in (select first_user_id from public.friends where (second_user_id = ?1 and invite_status ='PENDING'))" +
            "and profile_info_id in (select id from public.profile_info where CONCAT(surname, ' ', name) LIKE CONCAT('%', (?2),'%'))", nativeQuery = true)
    List<User> fiendReceivedInvitesWithSearch(User user, String searchLine, Pageable pageable);


    @Query(value = "select * from public.users mainuser \n" +
            "        where id not in ( select first_user_id\n" +
            "                             from public.friends\n" +
            "                             where (friends.second_user_id = ?1 and friends.invite_status ='ACCEPTED') \n" +
            "                             union select second_user_id\n" +
            "                             from public.friends\n" +
            "                             where (friends.first_user_id = ?1 and (friends.invite_status ='ACCEPTED' or friends.invite_status ='PENDING' )) )\n" +
            "                             and ( ( mainuser.username not like 'ADMIN') and ( mainuser.username not like :#{#user.username}))\n" +
            "          order by" +
            "               ( select count(*) FROM\n" +
            "                       ( select users.id FROM public.users " +
            "                               WHERE users.id in ?2 " +
            "                       ) one " +
            "               inner join" +
            "                       ( select users.id from public.friends, public.users\n" +
            "                               where (friends.invite_status ='ACCEPTED' and ((friends.second_user_id = mainuser.id and users.id = friends.first_user_id) or (friends.first_user_id = mainuser.id and users.id = friends.second_user_id)))\t\t\t \n" +
            "                       ) two \n" +
            "               using( id ) ) DESC LIMIT 5\n", nativeQuery = true)
    List<User> findPossibleFriendsByMutualFriends(User user, List<Long> friendIds);

    @Query(value = "     select count(*) FROM \n" +
            "                                   (  select users.id from public.friends, public.users " +
            "                                            where (friends.invite_status ='ACCEPTED' and ((friends.second_user_id = ?1 and users.id = friends.first_user_id) or (friends.first_user_id = ?1 and users.id = friends.second_user_id))) " +
            "                                   ) one " +
            "                           inner join \n" +
            "                                   ( select users.id from public.friends, public.users \n" +
            "                                           where (friends.invite_status ='ACCEPTED' and ((friends.second_user_id = ?2 and users.id = friends.first_user_id) or (friends.first_user_id = ?2 and users.id = friends.second_user_id)))  \n" +
            "                                   ) two  \n" +
            "                          using( id )", nativeQuery = true)
    Long findMutualFriends(Long user, Long possibleFriend);
}
