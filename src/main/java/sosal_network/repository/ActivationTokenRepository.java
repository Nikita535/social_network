package sosal_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sosal_network.entity.ActivationToken;


/**
 * Class ActivationTokenRepository - класс для основных действий с БД
 * **/
@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken,Integer> {

    /**
     * Метод ActivationToken для поиска пользователя по токену активации
     * param token - токен для активации
     * author - Nikita
     * **/
    ActivationToken findByToken(String token);

    /**
     * Метод deleteByToken для удаления пользователя по токену активации
     * param token - токен для активации
     * author - Nikita
     * **/
    void deleteByToken(String token);
}
