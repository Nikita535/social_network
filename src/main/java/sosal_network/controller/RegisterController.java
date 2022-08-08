package sosal_network.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sosal_network.entity.ActivationToken;
import sosal_network.entity.ProfileInfo;
import sosal_network.entity.User;
import sosal_network.repository.ActivationTokenRepository;
import sosal_network.service.UserService;
import sosal_network.utility.ReCaptchaResponse;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;


/**
 * Class RegisterController - класс контроллера для регистрации
 **/
@RequiredArgsConstructor
@Slf4j
@Controller
@SessionAttributes("user")
public class RegisterController {

    @ModelAttribute("user")
    public User user()
    {
        return new User();
    }

    /**
     * поле сервиса пользователя
     **/
    @Autowired
    private UserService userService;

    /**
     * Поле репозитория активационного токена
     **/
    @Autowired
    private ActivationTokenRepository activationTokenRepository;


    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    @Value("${recaptcha.url}")
    private String recaptchaURL;


    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Get контроллер для сбора данных из формы (thymeleaf)
     * author - Nikita
     **/
    @GetMapping("/register")
    public String getForm(Model model) {
//        User user = new User();
//        model.addAttribute("user", user);
        return "register";
    }

    /**
     * Post контроллер для валидации данных и сохранения пользователя
     * author - Nikita, Renat
     **/
    @PostMapping("/register")
    public String registerSave(HttpServletRequest request,
                               HttpServletResponse response,
                               @ModelAttribute("user") @Valid User user,
                               BindingResult bindingResult,
                               Model model, RedirectAttributes redirectAttributes) throws IOException {
        if (bindingResult.hasErrors())
        {
            return "/register";
        }
        else {
            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            if (!UserService.verifyReCAPTCHA(gRecaptchaResponse, recaptchaSecret, recaptchaURL, restTemplate)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
            return userService.validateRegister(user,bindingResult, model, redirectAttributes);
        }
    }

    @GetMapping("/register/again")
    public String registerAgain(@AuthenticationPrincipal User user,RedirectAttributes redirectAttributes)
    {
        redirectAttributes.addFlashAttribute("user",user);
        return "redirect:/registerContinue";
    }


    /**
     * Get контроллер отображения страницы для завершения регистрации
     * author - Nekit
     **/
    @GetMapping("/registerContinue")
    public String showRegisterContinue(Model model, @ModelAttribute("user") User user) {
        if (userService.findUserByUsername(user.getUsername()) == null)
        {
            return "/error-404";
        }
        ProfileInfo profileInfo = new ProfileInfo();
        model.addAttribute("profileInfo", profileInfo);
        return "registerContinue";
    }

    /**
     * Post контроллер для обработки данных о пользователе
     * author - Nekit
     **/
    @PostMapping("/registerContinue")
    public String saveProfileInfo(@ModelAttribute("user") User user, @RequestParam("date") String dateOfBirth, @ModelAttribute("profileInfo") @Valid ProfileInfo profileInfo, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, SessionStatus status) {
        if (bindingResult.hasErrors())
        {
            return "registerContinue";
        }
        else {
            return userService.addProfileInfo(profileInfo, redirectAttributes, user, dateOfBirth,status);
        }
    }

    /**
     * Get контроллер для страницы активации аккаунта
     * author - Nikita
     **/
    @GetMapping("/activate/{code}")
    public String activate(RedirectAttributes redirectAttributes, @PathVariable String code) {
        ActivationToken activationToken = activationTokenRepository.findByToken(code);
        if (activationToken != null && activationToken.compareDate()) {
            userService.activateUser(code);
            if (userService.getUserAuth() != null) {
                return "redirect:/";
            } else {
                redirectAttributes.addFlashAttribute("activateUser",true);
                return "redirect:/login";
            }
        } else
            return "invalidToken";
    }

}
