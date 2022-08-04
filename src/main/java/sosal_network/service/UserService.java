package sosal_network.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sosal_network.Enum.Role;
import sosal_network.entity.*;
import sosal_network.repository.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Class userService - класс для основных операций над пользователем
 **/
@RequiredArgsConstructor
@Slf4j
@Service
public class UserService implements UserDetailsService {
    /**
     * Bean репозитория пользователя
     **/
    @Autowired
    private UserRepository userRepository;

    /**
     * Bean репозитория кода активации
     **/
    @Autowired
    ActivationTokenRepository activationTokenRepository;

    /**
     * Bean репозитория кода для восстановления аккаунта
     **/
    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    /**
     * Bean репозитория для информации об аккаунте
     **/
    @Autowired
    private ProfileInfoRepository profileInfoRepository;

    /**
     * Bean класса кодирования паролей
     **/
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Bean сервиса для почты
     **/
    @Autowired
    EmailService emailService;

    @Autowired
    ImageRepository imageRepository;


    /**
     * Метод загрузки пользователя, наследованный UserDetailsService
     * param username - имя пользователя
     * author - Nikita
     **/
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            log.error("error login");
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    /**
     * метод поиска пользователя в БД
     * param username - имя пользователя
     * author - Nikita
     **/
    @Transactional
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void resaveUser(User user) {
        userRepository.save(user);
    }

    /**
     * Метод поиска пользователя по почте
     * param email - почта пользователя
     * author - Renat
     **/
    @Transactional
    public User findUserByEmail(String email) {
        return userRepository.findByUserEmail(email);
    }

    @Transactional
    public ProfileInfo findByUser_Username(String username) {
        return profileInfoRepository.findByUser_Username(username);
    }

    /**
     * Метод сохранения пользователя в БД
     * param User user - пользователь
     * author - Nikita and Nekit
     **/
    @Transactional
    public boolean saveUser(User user) throws MessagingException {
        User userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB != null) {
            return false;
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setActive(true);
        user.setRegistrationDate(LocalDate.now());
        userRepository.save(user);

        createActivationCode(user.getUserEmail());

        return true;
    }


    /**
     * Метод проверки правильности регистрации и сохранение пользователя в БД
     * param user - пользователь
     * param model - модель для добавления атрибутов на текущую страницу
     * param redirectAttributes - модель для добавления атрибутов на переадресованную страницу
     * author - Nikita, Nekit, Renat
     **/
    public String validateRegister(User user, Model model, RedirectAttributes redirectAttributes) {

        if (!Objects.equals(user.getPassword(), user.getUserPasswordConfirm())) {
            model.addAttribute("errorConfPassword", true);
            log.warn("error confirm pass");
            return "register";
        }
        if (user.getPassword().length() < 5) {
            model.addAttribute("errorLenPassword", true);
            log.warn("error pass length");
            return "register";
        }
        if (findUserByUsername(user.getUsername()) != null) {
            model.addAttribute("errorAlreadyExistsUsername", true);
            log.warn("error user already exists");
            return "register";
        }
        if (findUserByEmail(user.getUserEmail()) != null) {
            model.addAttribute("errorAlreadyExistsEmail", true);
            log.warn("mail already exists");
            return "register";
        }
        try {
            saveUser(user);
//            log.info("user add");
//            redirectAttributes.addFlashAttribute("registerSuccess", true);
            return "redirect:/register/info/" + user.getUsername();
        } catch (Exception e) {
            log.error(e.getClass().toString());
            return "register";
        }
    }

    /**
     * Метод для активации аккаунта пользователя
     * param code - токен активации
     * author - Nikita, Renat
     **/
    @Transactional
    public void activateUser(String code) {
        User user = activationTokenRepository.findByToken(code).getUser();
        if (user == null) {
            return;
        }
        user.setActive(true);
        activationTokenRepository.deleteByToken(code);
        userRepository.save(user);
    }

    /**
     * Метод для создания кода активации
     * param userEmail - почта пользователя
     * author - Nikita, Renat
     **/
    public void createActivationCode(String userEmail) throws MessagingException {
        User user = findUserByEmail(userEmail);
        String token = UUID.randomUUID().toString();
        ActivationToken myToken = new ActivationToken(token, user, new Date());
        activationTokenRepository.save(myToken);

        if (!StringUtils.isEmpty(user.getUserEmail())) {
            String message = "Привет, " + user.getUsername() + "!" +
                    " для активации аккаунта перейдите <a href='http://localhost:8080/activate/" + token + "'>по ссылке</a>";
            emailService.sendSimpleMessage(user.getUserEmail(), message);
        }
    }

    /**
     * Метод для создания кода восстановления аккаунта
     * param userEmail - почта пользователя
     * author - Renat, Nekit
     **/
    public void createPasswordResetTokenForUser(String userEmail) throws MessagingException {
        User user = findUserByEmail(userEmail);
        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken(token, user, new Date());
        passwordTokenRepository.save(myToken);

        if (!StringUtils.isEmpty(user.getUserEmail())) {
            String message = "Привет, " + user.getUsername() + "!" +
                    " для восстановление аккаунта перейдите <a href='http://localhost:8080/recover/" + token + "'>по ссылке</a>";
            emailService.sendSimpleMessage(user.getUserEmail(), message);
        }
    }

    /**
     * Метод смены пароля при попытке восстановления аккаунта
     * param token - код восстановления
     * param userPassword - пароль пользователя
     * param userPasswordConfirm - подтверждение пароля
     * author - Renat, Nekit
     **/
    @Transactional
    public String changePasswordByToken(String token, String userPassword, String userPasswordConfirm,
                                        RedirectAttributes redirectAttributes) {
        if (!Objects.equals(userPassword, userPasswordConfirm)) {
            redirectAttributes.addFlashAttribute("errorConfPassword", true);
            redirectAttributes.addFlashAttribute("token", token);
            log.warn("error confirm pass");
            return "redirect:/recoveryPage";
        }

        if (userPassword.length() < 5) {
            redirectAttributes.addFlashAttribute("errorLenPassword", true);
            redirectAttributes.addFlashAttribute("token", token);
            log.warn("error pass length");
            return "redirect:/recoveryPage";
        }

        try {
            User user = passwordTokenRepository.findByToken(token).getUser();
            user.setPassword(bCryptPasswordEncoder.encode(userPassword));
            userRepository.save(user);
            passwordTokenRepository.deleteByToken(token);
            log.info("password changed");
            return "redirect:/login";
        } catch (Exception e) {
            log.error(e.getClass().toString());
            return "redirect:/recovery";
        }
    }

    /**
     * Метод для добавления информации о пользователе
     * param profileInfo - информация о пользователе
     * param redirectAttributes - модель для добавления атрибутов на переадресованную страницу
     * param username - имя пользователя
     * author - Nekit
     **/
    @Transactional
    public String addProfileInfo(ProfileInfo profileInfo, RedirectAttributes redirectAttributes, String username, String dateOfBirth) {
        try {
            if (!Objects.equals(dateOfBirth, null)) {
                profileInfo.setDateOfBirth(LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            profileInfo.setUser(findUserByUsername(username));
            profileInfoRepository.save(profileInfo);
            redirectAttributes.addFlashAttribute("registerSuccess", true);
            return "redirect:/login";
        } catch (Exception e) {
            userRepository.deleteUserByUsername(username);
            return "redirect:/register";
        }
    }

    /**
     * Метод для получения информации о пользователе из сессии
     * author - Nekit
     **/
    @Transactional
    public User getUserAuth() {

        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    public Boolean getUserActive() {
        return getUserAuth().getActive();
    }

    public String editProfile(ProfileInfo editedProfile, String dateOfBirth, RedirectAttributes redirectAttributes,
                              String currentPassword, String newPassword,
                              String passwordConfirm,
                              MultipartFile file) throws IOException {
        ProfileInfo profileSession = findByUser_Username(getUserAuth().getUsername());

        if (Objects.equals(editedProfile.getName(), "") || Objects.equals(editedProfile.getCity(), "")
                || Objects.equals(editedProfile.getSurname(), "")
        ) {
            redirectAttributes.addFlashAttribute("errorLen", true);
            log.warn("error len of profile");
            return "redirect:/edit";
        }
        if (!Objects.equals(dateOfBirth, "")) {
            LocalDate changedDate = LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            editedProfile.setDateOfBirth(changedDate);
        }
        redirectAttributes.addFlashAttribute("profileChanged", true);
        editedProfile.setUser(profileSession.getUser());


        editedProfile.setId(profileSession.getId());
        profileSession = editedProfile;

        saveImage(file, profileSession);

        profileInfoRepository.save(profileSession);


        if (!Objects.equals(currentPassword, "") && !Objects.equals(newPassword, "")
                && !Objects.equals(passwordConfirm, "")) {
            if (!bCryptPasswordEncoder.matches(currentPassword, profileSession.getUser().getPassword())) {
                redirectAttributes.addFlashAttribute("errorCurrentPassword", true);
                log.warn("error current passwords");
                return "redirect:/edit";
            }

            if (!Objects.equals(newPassword, passwordConfirm)) {
                redirectAttributes.addFlashAttribute("errorPassword", true);
                log.warn("error passwords");
                return "redirect:/edit";
            }


            profileSession.getUser().setPassword(bCryptPasswordEncoder.encode(newPassword));
            redirectAttributes.addFlashAttribute("passwordChanged", true);
            userRepository.save(profileSession.getUser());

        }
        return "redirect:/edit";
    }


    private Image toImageEntity(MultipartFile file, ProfileInfo profileSession) throws IOException {
        return new Image(file.getName(), file.getOriginalFilename(), file.getSize(), file.getContentType(),
                file.getBytes(), profileSession.getUser(), true);
    }


    private void saveImage(MultipartFile file, ProfileInfo profileSession) throws IOException {
        if (file.getSize() != 0) {
            if (!getUserAuth().getImages().isEmpty()) {
                Image image = imageRepository.findImageByUser(profileSession.getUser());
                Image img = toImageEntity(file, profileSession);
                img.setId(image.getId());
                img.setUser(image.getUser());
                image = img;
                profileSession.getUser().addImageToUser(image);
                imageRepository.save(image);
            } else {
                getUserAuth().addImageToUser(toImageEntity(file, profileSession));
                imageRepository.save(toImageEntity(file, profileSession));
            }
        }
    }


}
