package sosal_network.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
<<<<<<<<< Temporary merge branch 1
import org.springframework.web.bind.annotation.*;
=========
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
>>>>>>>>> Temporary merge branch 2
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sosal_network.entity.ActivationToken;
import sosal_network.entity.ProfileInfo;
import sosal_network.entity.User;
import sosal_network.repository.ActivationTokenRepository;
import sosal_network.service.UserService;
import sosal_network.utility.ReCaptchaResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;


/**
 * Class RegisterController - класс контроллера для регистрации
 **/
@RequiredArgsConstructor
@Slf4j
@Controller
public class RegisterController {

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
        User user = new User();
        model.addAttribute("user", user);
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
                               Model model, RedirectAttributes redirectAttributes) throws IOException {

        String gRecaptchaResponse=request.getParameter("g-recaptcha-response");
        if(!UserService.verifyReCAPTCHA(gRecaptchaResponse,recaptchaSecret,recaptchaURL,restTemplate)){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

        return userService.validateRegister(user, model, redirectAttributes);
    }


    /**
     * Get контроллер отображения страницы для завершения регистрации
     * author - Nekit
     **/
    @GetMapping("/registerContinue")
    public String showRegisterContinue(Model model) {
        ProfileInfo profileInfo = new ProfileInfo();
        model.addAttribute("profileInfo", profileInfo);
        return "registerContinue";
    }

    /**
     * Get контроллер для завершения регистрации
     * author - Nekit
     **/
    @GetMapping("/register/info/{username}")
    public String getRegisterContinue(@PathVariable String username, RedirectAttributes redirectAttributes) {
        if (userService.findByUser_Username(username) != null) {
            return "/error-404";
        }
        redirectAttributes.addFlashAttribute("username", username);
        return "redirect:/registerContinue";
    }

    /**
     * Post контроллер для обработки данных о пользователе
     * author - Nekit
     **/
    @PostMapping("/register/info/{username}")
    public String saveProfileInfo(@ModelAttribute("profileInfo") ProfileInfo profileInfo, RedirectAttributes redirectAttributes, @PathVariable String username, @RequestParam("date") String dateOfBirth) {
        return userService.addProfileInfo(profileInfo, redirectAttributes, username,dateOfBirth);
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
