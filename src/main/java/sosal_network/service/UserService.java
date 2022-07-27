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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sosal_network.Enum.Role;
import sosal_network.entity.PasswordResetToken;
import sosal_network.entity.User;
import sosal_network.repository.UserRepository;
import sosal_network.repository.passwordTokenRepository;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private passwordTokenRepository passwordTokenRepository;
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
    public User findUserByEmail(String email){
        return userRepository.findByUserEmail(email);
    }



    @Transactional
    public boolean saveUser(User user){
        User userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB != null){
            return false;
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setActive(false);
        user.setActivationCode(UUID.randomUUID().toString());
        userRepository.save(user);


        if (!StringUtils.isEmpty(user.getUserEmail())) {
            String message = "Привет, "+user.getUsername()+"!"+
                    " Для подтверждения своей почты перейдите по ссылке http://localhost:8080/activate/"
                    + user.getActivationCode();
            emailService.sendSimpleMessage(user.getUserEmail(), message);
        }


        return true;
    }



    @Transactional
    public String validateRegister(User user, Model model, RedirectAttributes redirectAttributes){

        if (!Objects.equals(user.getPassword(), user.getUserPasswordConfirm())){
            redirectAttributes.addFlashAttribute("errorConfPassword", true);
            log.warn("error confirm pass");
            return "redirect:/register";
        }

        if (findUserByEmail(user.getUserEmail()) != null){
            redirectAttributes.addFlashAttribute("errorAlreadyExistsEmail", true);
            log.warn("error email already exists");
            return "redirect:/register";
        }

        if (user.getPassword().length() < 5){
            redirectAttributes.addFlashAttribute("errorLenPassword", true);
            log.warn("error pass length");
            return "redirect:/register";
        }
        if (findUserByUsername(user.getUsername()) != null){
            redirectAttributes.addFlashAttribute("errorAlreadyExistsUsername", true);
            log.warn("error user already exists");
            return "redirect:/register";
        }
        try{
            saveUser(user);
            log.info("user add");
            return "redirect:/login";
        } catch (Exception e){
            log.error(e.getClass().toString());
            return "redirect:/register";
        }
    }

    public void activateUser(String code) {
        User user = userRepository.findUserByActivationCode(code);
        if (user.getActivationCode() == null){
            return;
        }
        user.setActive(true);
        user.setActivationCode(null);
        userRepository.save(user);
    }

    public void createPasswordResetTokenForUser(String userEmail) {
        User user = findUserByEmail(userEmail);
        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken(token, user, new Date());
        passwordTokenRepository.save(myToken);

        if (!StringUtils.isEmpty(user.getUserEmail())) {
            String message = "Привет, "+user.getUsername()+"!"+
                    " для восстановление аккаунта перейдите по ссылке http://localhost:8080/recover/"
                    +token;
            emailService.sendSimpleMessage(user.getUserEmail(), message);
        }
    }
    @Transactional
    public String changePasswordByToken(String token, String userPassword, String userPasswordConfirm,
                                        RedirectAttributes redirectAttributes) {
        if (!Objects.equals(userPassword, userPasswordConfirm)) {
            redirectAttributes.addFlashAttribute("errorConfPassword", true);
            redirectAttributes.addFlashAttribute("token", token);
            log.warn("error confirm pass");
            return "redirect:/recoveryPage";
        }

        if (userPassword.length() < 5){
            redirectAttributes.addFlashAttribute("errorLenPassword", true);
            redirectAttributes.addFlashAttribute("token", token);
            log.warn("error pass length");
            return "redirect:/recoveryPage";
        }

        try{
            User user = passwordTokenRepository.findByToken(token).getUser();
            user.setPassword(bCryptPasswordEncoder.encode(userPassword));
            userRepository.save(user);
            passwordTokenRepository.deleteByToken(token);
            log.info("password changed");
            return "redirect:/login";
        } catch (Exception e){
            log.error(e.getClass().toString());
            return "redirect:/recovery";
        }
    }
}
