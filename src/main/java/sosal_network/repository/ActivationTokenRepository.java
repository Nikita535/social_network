package sosal_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sosal_network.entity.ActivationToken;
import sosal_network.entity.PasswordResetToken;

@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken,Integer> {
    ActivationToken findByToken(String token);
    void deleteByToken(String token);
}
