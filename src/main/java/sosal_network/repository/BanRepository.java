package sosal_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sosal_network.entity.BanInfo;

@Repository

public interface BanRepository extends JpaRepository<BanInfo, Long> {

    BanInfo findBanInfoById(Long id);
}
