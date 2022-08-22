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
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AdminService {

    @Autowired
    UserService userService;

    @Autowired
    BanRepository banRepository;

    @Loggable
    public void banUser(String username,BanStatus banType) throws MessagingException {
        User user = userService.findUserByUsername(username);
        BanInfo banInfo= banRepository.findBanInfoById(user.getBanInfo().getId());
        if(!user.getRoles().contains(Role.ROLE_ADMIN)) {
            banInfo.setBanStatus(true);
            banInfo.setBanTime(banType);
            banRepository.save(banInfo);
        }
    }

    @Loggable
    public void unBanUser(String username) throws MessagingException {
        User user = userService.findUserByUsername(username);
        BanInfo banInfo= banRepository.findBanInfoById(user.getBanInfo().getId());
        if(!user.getRoles().contains(Role.ROLE_ADMIN)) {
            banInfo.setBanStatus(false);
            banInfo.setBanTime(BanStatus.NONE);
            banRepository.save(banInfo);
        }
    }


    @Loggable
    public void banUserForTimer(String username,int timeOfBan,BanStatus banType) throws MessagingException {
        long ban = timeOfBan * 60000L;

        User user = userService.findUserByUsername(username);
        BanInfo banInfo= banRepository.findBanInfoById(user.getBanInfo().getId());

        if(!user.getRoles().contains(Role.ROLE_ADMIN)) {
            banInfo.setBanStatus(true);
            banInfo.setBanTime(banType);
            banRepository.save(banInfo);

            Timer timer=new Timer();
                timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            banInfo.setBanStatus(false);
                            banInfo.setBanTime(BanStatus.NONE);
                            banRepository.save(banInfo);
                        }
                    }
            ,ban);
        }
    }


    @Loggable
    public void checkBanTime(String banTime,String username) throws MessagingException {
        switch (banTime) {
            case ("INF") -> banUser(username,BanStatus.INF);
            case ("HALF_HOUR") -> banUserForTimer(username, 30,BanStatus.HALF_HOUR);
            case ("HOUR") -> banUserForTimer(username, 60,BanStatus.HOUR);
            case ("DAY") -> banUserForTimer(username, 1440,BanStatus.DAY);
            case ("WEEK") -> banUserForTimer(username, 10080,BanStatus.WEEK);
            case ("MONTH") -> banUserForTimer(username, 43200,BanStatus.MONTH);
        }
    }

}
