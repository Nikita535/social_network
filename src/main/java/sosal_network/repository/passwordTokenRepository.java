package sosal_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sosal_network.entity.PasswordResetToken;
import sosal_network.entity.User;

public interface passwordTokenRepository extends JpaRepository<PasswordResetToken,Integer> {
    PasswordResetToken findByToken(String token);

    void deleteByToken(String token);

}
