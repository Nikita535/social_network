package sosal_network.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sosal_network.entity.ActivationToken;
import sosal_network.entity.User;
import sosal_network.repository.ActivationTokenRepository;
import sosal_network.service.UserService;

import javax.validation.Valid;


/**
 * Class RegisterController - класс контроллера для регистрации
 * **/
@RequiredArgsConstructor
@Slf4j
@Controller
public class RegisterController {

    /**
     * поле сервиса пользователя
     * **/
    @Autowired
    private UserService userService;

    /**
     * Поле репозитория активационного токена
     * **/
    @Autowired
    private ActivationTokenRepository activationTokenRepository;


    /**
     * Get контроллер для сбора данных из формы (thymeleaf)
     * author - Nikita
     * **/
    @GetMapping("/register")
    public String getForm(Model model){
        User user = new User();
        model.addAttribute("user", user);
        return "register";
    }

    /**
     * Post контроллер для валидации данных и сохранения пользователя
     * author - Nikita, Renat
     * **/
    @PostMapping("/register")
    public String registerSave(@ModelAttribute("user") @Valid User user,
                               Model model, RedirectAttributes redirectAttributes) {
        return userService.validateRegister(user, model,redirectAttributes);
    }

    /**
     *  Get контроллер для страницы активации аккаунта
     *  author - Nikita
     *  **/
    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        ActivationToken activationToken = activationTokenRepository.findByToken(code);
        if (activationToken != null && activationToken.compareDate()) {
            userService.activateUser(code);
            return "redirect:/login";
        }
        else
            return "invalidToken";
    }

}
