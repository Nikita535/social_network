package sosal_network.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sosal_network.Enum.Role;
import sosal_network.aop.LoggableAroundMethod.Loggable;
import sosal_network.entity.User;

import javax.mail.MessagingException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AdminService {

    @Autowired
    UserService userService;

    @Loggable
    public void banUser(String username) throws MessagingException {
        User user = userService.findUserByUsername(username);
        if(!user.getRoles().contains(Role.ROLE_ADMIN)) {
            user.setBanStatus(true);
            userService.saveUser(user);
        }
    }

    @Loggable
    public void unBanUser(String username) throws MessagingException {
        User user = userService.findUserByUsername(username);
        if(!user.getRoles().contains(Role.ROLE_ADMIN)) {
            user.setBanStatus(false);
            userService.saveUser(user);
        }
    }

    @Deprecated
    @Loggable
    public void banUserForTime(String username) throws MessagingException {
        User user = userService.findUserByUsername(username);
        if(!user.getRoles().contains(Role.ROLE_ADMIN)) {
            user.setBanStatus(true);
            userService.saveUser(user);

            try{
                TimeUnit.MINUTES.sleep(2);
                user.setBanStatus(false);
                userService.saveUser(user);
            }catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Loggable
    public void banUserForTimer(String username,int timeOfBan) throws MessagingException {
        long ban = timeOfBan * 60000L;

        User user = userService.findUserByUsername(username);
        if(!user.getRoles().contains(Role.ROLE_ADMIN)) {
            user.setBanStatus(true);
            userService.saveUser(user);

            Timer timer=new Timer();
                timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            user.setBanStatus(false);
                            userService.save(user);
                        }
                    }
            ,ban);
        }
    }


    @Loggable
    public void checkBanTime(String banTime,String username) throws MessagingException {
        int timeOfBan =Integer.parseInt(banTime);
        switch (Integer.parseInt(banTime)){
            case (0):
                banUser(username);
                log.info("ban for inf");
            default:
                banUserForTimer(username,timeOfBan);
                log.info("ban for "+timeOfBan);
        }
    }

}
