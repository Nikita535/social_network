package sosal_network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sosal_network.entity.PasswordResetToken;
import sosal_network.repository.PasswordTokenRepository;
import sosal_network.service.EmailService;
import sosal_network.service.UserService;

import javax.mail.MessagingException;


/**
 * Class RecoveryController - контроллер для восстановления пароля
 **/
@Controller
public class RecoveryController {

    /**
     * Поле репозитория токена восстановления
     **/
    @Autowired
    private PasswordTokenRepository passwordTokenRepository;


    /**
     * Поле сервиса почты
     **/
    @Autowired
    EmailService emailService;

    /**
     * Поле сервиса пользователя
     **/
    @Autowired
    private UserService userService;


    @GetMapping("/recovery")
    public String getRecoveryForm() {
        return "recovery";
    }

    @PostMapping("/recovery")
    public String sendRecoveryToken(@RequestParam("email") String userEmail) throws MessagingException {
        userService.createPasswordResetTokenForUser(userEmail);
        return "login";
    }

    @GetMapping("/recover/{token}")
    public String getRecoveryToken(@PathVariable String token, RedirectAttributes redirectAttributes) {
        PasswordResetToken resetToken = passwordTokenRepository.findByToken(token);
        if (resetToken != null && resetToken.compareDate()) {
            redirectAttributes.addFlashAttribute("token", resetToken.getToken());
            redirectAttributes.addFlashAttribute("username", resetToken.getUser().getUsername());
            return "redirect:/recoveryPage";
        } else return "invalidToken";
    }

    @GetMapping("/recoveryPage")
    public String getRecoveryByPassword(@ModelAttribute("token") String token) {
        return "recoveryPage";
    }

    @PostMapping("/recoveryPage/{token}")
    public String getRecoveryByPassword(@PathVariable String token, @RequestParam("password") String userPassword, @RequestParam("passwordConfirm") String passwordConfirm, RedirectAttributes redirectAttributes) {
        return userService.changePasswordByToken(token, userPassword, passwordConfirm, redirectAttributes);
    }

}
