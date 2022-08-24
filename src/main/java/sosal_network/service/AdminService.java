package sosal_network.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sosal_network.Enum.BanStatus;
import sosal_network.Enum.Role;
import sosal_network.aop.LoggableAroundMethod.Loggable;
import sosal_network.entity.BanInfo;
import sosal_network.entity.User;
import sosal_network.repository.BanRepository;

import javax.mail.MessagingException;
import java.util.Timer;
import java.util.TimerTask;

@Service
@Slf4j
public class AdminService {

    @Autowired
    UserService userService;

    @Autowired
    BanRepository banRepository;

    @Loggable
    public void banUser(String username, BanStatus banType, String banCause) throws MessagingException {
        User user = userService.findUserByUsername(username);
        BanInfo banInfo = banRepository.findBanInfoById(user.getBanInfo().getId());
        if (!user.getRoles().contains(Role.ROLE_ADMIN)) {
            banInfo.setBanStatus(true);
            banInfo.setBanTime(banType);
            banInfo.setBanCause(banCause);
            banRepository.save(banInfo);
        }
    }

    @Loggable
    public void unBanUser(String username) throws MessagingException {
        User user = userService.findUserByUsername(username);
        BanInfo banInfo = banRepository.findBanInfoById(user.getBanInfo().getId());
        if (!user.getRoles().contains(Role.ROLE_ADMIN)) {
            banInfo.setBanStatus(false);
            banInfo.setBanTime(BanStatus.NONE);
            banInfo.setBanCause("");
            banRepository.save(banInfo);
        }
    }


    @Loggable
    public void banUserForTimer(String username, int timeOfBan, BanStatus banType, String banCause) throws MessagingException {
        long ban = timeOfBan * 60000L;

        User user = userService.findUserByUsername(username);
        BanInfo banInfo = banRepository.findBanInfoById(user.getBanInfo().getId());

        if (!user.getRoles().contains(Role.ROLE_ADMIN)) {
            banInfo.setBanStatus(true);
            banInfo.setBanTime(banType);
            banInfo.setBanCause(banCause);
            banRepository.save(banInfo);

            Timer timer = new Timer();
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            banInfo.setBanStatus(false);
                            banInfo.setBanTime(BanStatus.NONE);
                            banInfo.setBanCause("");
                            banRepository.save(banInfo);
                        }
                    }
                    , ban);
        }
    }


    @Loggable
    public void checkBanTime(String banTime, String username) throws MessagingException {
        switch (banTime) {
            case ("INF") -> banUser(username, BanStatus.INF, "Серьезное нарушение!");
            case ("HALF_HOUR") -> banUserForTimer(username, 30, BanStatus.HALF_HOUR, "Мелкое нарушение");
            case ("HOUR") -> banUserForTimer(username, 60, BanStatus.HOUR, "Говна поел");
            case ("DAY") -> banUserForTimer(username, 1440, BanStatus.DAY, "Оскорбление личности");
            case ("WEEK") -> banUserForTimer(username, 10080, BanStatus.WEEK, "Распространение эротического контента");
            case ("MONTH") -> banUserForTimer(username, 43200, BanStatus.MONTH, "Если ты Ренат");
        }
    }

    @Loggable
    public String translateBanTime(BanStatus banStatus) {
        return switch (banStatus.toString()) {
            case "HALF_HOUR" -> "пол часа";
            case "HOUR" -> "1 час";
            case "DAY" -> "1 день";
            case "WEEK" -> "1 неделю";
            case "MONTH" -> "1 месяц";
            default -> "бесконечность";
        };
    }

}
