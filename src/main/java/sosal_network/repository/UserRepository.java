package sosal_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sosal_network.entity.User;

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


}
