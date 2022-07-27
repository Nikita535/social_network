package sosal_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sosal_network.entity.User;

import java.util.List;


public interface UserRepository extends JpaRepository<User,Integer> {
    User findByUsername(String username);
    User findByUserEmail(String username);
    User findUserByActivationCode(String code);

}
