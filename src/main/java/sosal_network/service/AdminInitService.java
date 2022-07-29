package sosal_network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import sosal_network.Enum.Role;
import sosal_network.entity.User;
import sosal_network.repository.UserRepository;

@Service
public class AdminInitService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserService userService;

    public AdminInitService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        String password = bCryptPasswordEncoder.encode("ADMIN");
        User admin = new User("ADMIN", password, password, "victor.hodinsciy.com@gmail.com");
        admin.getRoles().add(Role.ROLE_ADMIN);
        admin.setActive(true);
        if (userRepository.findByUsername("ADMIN") == null) {
            userRepository.save(admin);
        }
        System.out.println(admin.getRoles().toArray()[0].toString().getClass());
    }


}
