package sosal_network.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sosal_network.Enum.BanStatus;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ban_info")
public class BanInfo {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "ban_cause")
    private String banCause;

    @Column(name = "ban_time")
    @Enumerated(EnumType.STRING)
    private BanStatus banTime;

    @Column(name = "ban_status")
    private boolean banStatus;

    public BanInfo(String banCause, BanStatus banTime, boolean banStatus) {
        this.banCause = banCause;
        this.banTime = banTime;
        this.banStatus = banStatus;
    }
}
