package sosal_network.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import sosal_network.Enum.Role;
import sosal_network.entity.User;
import sosal_network.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    EmailService emailService;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null){
            log.error("error login");
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    @Transactional
    public User findUserByUsername(String username){
        return userRepository.findByUsername(username);
    }



    @Transactional
    public void updateUserPassword(User user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }


    @Transactional
    public boolean saveUser(User user){
        User userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB != null){
            return false;
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        userRepository.save(user);

        if (!StringUtils.isEmpty(user.getUserEmail())) {
            String message = String.format(
                    "Привет, %s! \n" + "Добро пожаловать. Пожалуйста, перейдите по ссылке: http://localhost:8080/activate/%s",
                    user.getUsername()
            );

            emailService.sendSimpleMessage(user.getUserEmail(), message);
        }


        return true;
    }



    @Transactional
    public String validateRegister(User user, Model model){

        if (!Objects.equals(user.getPassword(), user.getUserPasswordConfirm())){
            model.addAttribute("errorConfPassword", true);
            log.warn("error confirm pass");
            return "register";
        }
        if (user.getPassword().length() < 5){
            model.addAttribute("errorLenPassword", true);
            log.warn("error pass length");
            return "register";
        }
        if (findUserByUsername(user.getUsername()) != null){
            model.addAttribute("errorAlreadyExistsUsername", true);
            log.warn("error user already exists");
            return "register";
        }
        try{
            saveUser(user);
            log.info("user add");
            return "redirect:/login";
        } catch (Exception e){
            log.error(e.getClass().toString());
            return "register";
        }
    }

}
