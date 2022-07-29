package sosal_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sosal_network.entity.PasswordResetToken;

/**
 * Class PasswordTokenRepository - класс для основных действий с БД
 **/
@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    /**
     * Метод findByToken для поиска пользователя по токену
     * param token - токен для сброса пароля
     * author - Renat
     **/
    PasswordResetToken findByToken(String token);

    /**
     * Метод deleteByToken для удаления пользователя по токену
     * param token - токен для сброса пароля
     * author - Renat
     **/
    void deleteByToken(String token);

}
