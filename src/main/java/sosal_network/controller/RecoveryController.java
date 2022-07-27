package sosal_network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sosal_network.entity.PasswordResetToken;
import sosal_network.entity.User;
import sosal_network.repository.UserRepository;
import sosal_network.repository.passwordTokenRepository;
import sosal_network.service.EmailService;
import sosal_network.service.UserService;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.Date;
import java.util.UUID;


@Controller
public class RecoveryController {
    @Autowired
    private passwordTokenRepository passwordTokenRepository;

    @Autowired
    EmailService emailService;
    @Autowired
    private UserService userService;


    @GetMapping("/recovery")
    public String getRecoveryForm(Model model){
        return "recovery";
    }

    @PostMapping("/recovery")
    public String sendRecoveryToken(@RequestParam("email") String userEmail,
                               Model model) throws MessagingException {
        userService.createPasswordResetTokenForUser(userEmail);
        return "login";
    }

    @GetMapping("/recover/{token}")
    public String getRecoveryToken(Model model, @PathVariable String token, RedirectAttributes redirectAttributes){
        PasswordResetToken resetToken = passwordTokenRepository.findByToken(token);
        if (resetToken != null && resetToken.compareDate()) {
            redirectAttributes.addFlashAttribute("token", resetToken.getToken());
            return "redirect:/recoveryPage";
        }
        else
            return "invalidToken";
    }

    @GetMapping("/recoveryPage")
    public String getRecoveryByPassword(Model model, @ModelAttribute("token") String token){
        model.addAttribute("token", token);
        return "recoveryPage";
    }

    @PostMapping("/recoveryPage/{token}")
    public String getRecoveryByPassword(Model model, @PathVariable String token,
                                        @RequestParam("password") String userPassword,
                                        @RequestParam("passwordConfirm") String passwordConfirm,
                                        RedirectAttributes redirectAttributes){
        return userService.changePasswordByToken(token, userPassword, passwordConfirm, redirectAttributes);
    }

}
