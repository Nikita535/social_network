package sosal_network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import sosal_network.Enum.Role;
import sosal_network.aop.LoggableAroundMethod.Loggable;
import sosal_network.entity.ProfileInfo;
import sosal_network.entity.User;
import sosal_network.repository.ProfileInfoRepository;
import sosal_network.repository.UserRepository;

import java.time.LocalDate;

@Service
public class AdminInitService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserService userService;

    private final ProfileInfoRepository profileInfoRepository;


    public AdminInitService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                            UserService userService, ProfileInfoRepository profileInfoRepository) {
        this.profileInfoRepository = profileInfoRepository;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    @Loggable
    public void run(String... args) throws Exception {
        String password = bCryptPasswordEncoder.encode("ADMIN");
        User admin = new User("ADMIN", password, password, "victor.hodinsciy.com@gmail.com");
        admin.getRoles().add(Role.ROLE_ADMIN);
        admin.setActive(true);
        admin.setBanStatus(false);
        if (userRepository.findByUsername("ADMIN") == null) {
            userRepository.save(admin);
            ProfileInfo profileInfoAdmin=new ProfileInfo(admin,"ADMIN","ADMIN","Москва",
                    LocalDate.now(),"","");
            profileInfoRepository.save(profileInfoAdmin);
        }

         //40 пользователей
        //for(int i = 0; i < 40; i++) {
        //    User user= new User("renat0" + i, password, password, "victor." + i + "@gmail.com");
        //    user.setActive(true);
        //    user.getRoles().add(Role.ROLE_USER);
        //    userRepository.save(user);
        //    ProfileInfo profileInfo = new ProfileInfo(user, "Ренат" + i, "Хакимов" + i, "город" + i,
        //            LocalDate.of(2020, 1, 8), "das", "Dsa");
        //    profileInfoRepository.save(profileInfo);
        //}
        //System.out.println(admin.getRoles().toArray()[0].toString().getClass());
    }


}
