package sosal_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sosal_network.entity.BanInfo;

public interface BanRepository extends JpaRepository<BanInfo,Long> {

    BanInfo findBanInfoById(Long id);
}
