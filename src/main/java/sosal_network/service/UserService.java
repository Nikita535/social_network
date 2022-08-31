package sosal_network.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sosal_network.Enum.BanStatus;
import sosal_network.Enum.Role;
import sosal_network.aop.LoggableAroundMethod.Loggable;
import sosal_network.entity.*;
import sosal_network.repository.*;
import sosal_network.utility.ReCaptchaResponse;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


/**
 * Class userService - класс для основных операций над пользователем
 **/
@RequiredArgsConstructor
@Slf4j
@Service
public class UserService implements UserDetailsService {

    private static final int USER_PAGE_SIZE=10;
    private static final int INVITE_PAGE_SIZE=5;
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
    private EmailService emailService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private BanRepository banRepository;


    /**
     * Метод загрузки пользователя, наследованный UserDetailsService
     * param username - имя пользователя
     * author - Nikita
     **/
    @Override
    @Transactional
    @Loggable
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUserByUsername(username);
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
    @Loggable
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    @Loggable
    public void save(User user) {
        userRepository.saveAndFlush(user);
    }

    /**
     * Метод поиска пользователя по почте
     * param email - почта пользователя
     * author - Renat
     **/
    @Transactional
    @Loggable
    public User findUserByEmail(String email) {
        return userRepository.findByUserEmail(email);
    }


    public User findUserById(Long id) {
        return userRepository.findUserById(id);
    }

    @Transactional
    public Page<User> findFriendUsers(User user, int page) {
        return userRepository.findFriendUsers(user, PageRequest.of(page, USER_PAGE_SIZE));
    }

    @Transactional
    public Page<User> findFriendUsersWithSearch(User user, String searchLine, int page) {
        return userRepository.findFriendUsersWithSearch(user, searchLine, PageRequest.of(page, USER_PAGE_SIZE));
    }

    @Transactional
    public List<User> findStrangers(User user, int page) {
        return userRepository.findStrangers(user, PageRequest.of(page, USER_PAGE_SIZE));
    }

    @Transactional
    public List<User> findStrangersWithSearch(User user, String searchLine, int page) {
        return userRepository.findStrangersWithSearch(user, searchLine, PageRequest.of(page, USER_PAGE_SIZE));
    }

    @Transactional
    public List<User> fiendReceivedInvites(User user, int page) {
        return userRepository.fiendReceivedInvites(user, PageRequest.of(page, INVITE_PAGE_SIZE));
    }

    @Transactional
    public List<User> fiendReceivedInvitesWithSearch(User user, String searchLine, int page) {
        return userRepository.fiendReceivedInvitesWithSearch(user, searchLine, PageRequest.of(page, INVITE_PAGE_SIZE));
    }


    /**
     * Метод сохранения пользователя в БД
     * param User user - пользователь
     * author - Nikita and Nekit
     **/
    @Loggable
    @Transactional
    public void saveUser(User user) throws MessagingException {
        User userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB != null) {
            return;
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setActive(true);
        user.setRegistrationDate(LocalDate.now());
        user.setBanInfo(new BanInfo("", BanStatus.NONE, false));
        save(user);
    }


    /**
     * Метод проверки правильности регистрации и сохранение пользователя в БД
     * param user - пользователь
     * param model - модель для добавления атрибутов на текущую страницу
     * param redirectAttributes - модель для добавления атрибутов на переадресованную страницу
     * author - Nikita, Nekit, Renat
     **/
    @Loggable
    public String validateRegister(User user, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        if (!Objects.equals(user.getPassword(), user.getPasswordConfirm())) {
            bindingResult.addError(new FieldError("user", "passwordConfirm", "Пароли не совпадают"));
            log.warn("error confirm pass");
        }
        if (findUserByUsername(user.getUsername()) != null) {
            bindingResult.addError(new FieldError("user", "username", "Пользователь с таким никнеймом уже существует"));
            log.warn("error user already exists");
        }
        if (findUserByEmail(user.getUserEmail()) != null) {
            bindingResult.addError(new FieldError("user", "userEmail", "Пользователь с такой почтой уже существует"));
            log.warn("mail already exists");
        }
        if (bindingResult.hasErrors()) {
            return "/register";
        }
        try {
            saveUser(user);
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/registerContinue";
        } catch (Exception e) {
            log.error(e.getClass().toString());
            return "/register";
        }
    }

    /**
     * Метод для активации аккаунта пользователя
     * param code - токен активации
     * author - Nikita, Renat
     **/
    @Loggable
    @Transactional
    public void activateUser(String code) {
        User user = activationTokenRepository.findByToken(code).getUser();
        if (user == null) {
            return;
        }
        user.setActive(true);
        activationTokenRepository.deleteByToken(code);
        save(user);
    }

    /**
     * Метод для создания кода активации
     * param userEmail - почта пользователя
     * author - Nikita, Renat
     **/
    @Loggable
    public void createActivationCode(String userEmail) throws MessagingException {
        User user = findUserByEmail(userEmail);
        String token = UUID.randomUUID().toString();
        ActivationToken myToken = new ActivationToken(token, user, new Date());
        activationTokenRepository.save(myToken);

        if (!ObjectUtils.isEmpty(user.getUserEmail())) {
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
    @Loggable
    public void createPasswordResetTokenForUser(String userEmail) throws MessagingException {
        User user = findUserByEmail(userEmail);
        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken(token, user, new Date());
        passwordTokenRepository.save(myToken);

        if (!ObjectUtils.isEmpty(user.getUserEmail())) {
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
    @Loggable
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
            save(user);
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
    @Loggable
    @Transactional
    public String addProfileInfo(ProfileInfo profileInfo, RedirectAttributes redirectAttributes, User user, SessionStatus status) {
        try {
            user.setProfileInfo(profileInfo);
            save(user);
            createActivationCode(user.getUserEmail());
            redirectAttributes.addFlashAttribute("registerSuccess", true);
            status.setComplete();
            return "redirect:/login";
        } catch (Exception e) {
            log.error(e.toString());
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/registerContinue";
        }
    }


    /**
     * Метод для получения информации о пользователе из сессии
     * author - Nekit
     **/
    @Loggable
    @Transactional
    public User getUserAuth() {

        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Loggable
    public String editProfile(ProfileInfo editedProfile, RedirectAttributes redirectAttributes,
                              User user) {

        ProfileInfo profileSession = user.getProfileInfo();
        user.setProfileInfo(editedProfile);
        save(user);
        profileInfoRepository.deleteById(profileSession.getId());
        redirectAttributes.addFlashAttribute("profileChanged", true);
        return "redirect:/edit";
    }

    @Loggable
    public String changePhoto(RedirectAttributes redirectAttributes,
                              MultipartFile file, User user) throws IOException {
        imageService.saveImage(file, user);
        redirectAttributes.addFlashAttribute("photoChanged", true);
        return "redirect:/edit";
    }

    @Loggable
    public String changePassword(User user, String currentPassword, String newPassword,
                                 String passwordConfirm, RedirectAttributes redirectAttributes) {
        if (!Objects.equals(currentPassword, "") && !Objects.equals(newPassword, "")
                && !Objects.equals(passwordConfirm, "")) {
            if (!bCryptPasswordEncoder.matches(currentPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute("errorCurrentPassword", true);
                log.warn("error current passwords");
                return "redirect:/edit";
            }

            if (!Objects.equals(newPassword, passwordConfirm)) {
                redirectAttributes.addFlashAttribute("errorPassword", true);
                log.warn("error passwords");
                return "redirect:/edit";
            }

            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            redirectAttributes.addFlashAttribute("passwordChanged", true);
            save(user);

        }
        return "redirect:/edit";
    }


    @Loggable
    public static boolean verifyReCAPTCHA(String gRecaptchaResponse, String recaptchaSecret, String recaptchaURL, RestTemplate restTemplate) {
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("secret", recaptchaSecret);
        map.add("response", gRecaptchaResponse);


        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ReCaptchaResponse response = restTemplate.postForObject(recaptchaURL, request,
                ReCaptchaResponse.class);

        assert response != null;
        if (response.getErrorCodes() != null) {
            for (String error : response.getErrorCodes()) {
                log.error(error);
            }
        }

        return response.isSuccess();
    }

    @Loggable
    public List<User> showAllUser() {
        return userRepository.findAll();
    }


    @Loggable
    public Boolean checkRole(User user) {
        return user.getRoles().contains(Role.ROLE_ADMIN);
    }
}
