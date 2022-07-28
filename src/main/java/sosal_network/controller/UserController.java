package sosal_network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sosal_network.entity.User;
import sosal_network.service.UserService;

import java.util.Optional;

/**
 * Class UserController - контроллер для страницы пользователя
 **/
@Controller
public class UserController {
    /**
     * Поле сервиса пользователя
     **/
    @Autowired
    private UserService userService;

    /**
     * Get контроллер для перенаправления пользователя на страницу профиля после авторизации
     * author - Nekit
     **/
    @GetMapping("/")
    public String getUser() {
        return "redirect:/user/" + userService.getUserAuth().getUsername();
    }

    /**
     * Get контроллер для страницы профиля пользователя
     * param username - имя пользователя
     * param model - модель для добавления атрибутов на текущую страницу
     * author - Nekit
     **/
    @GetMapping({"/user/{username}"})
    public String getHome(@PathVariable Optional<String> username, Model model) {
        if (userService.getUserAuth() == null && (username.isEmpty())) {
            return "/error";
        }
        if (username.isEmpty()) {
            String usernameAuth = userService.getUserAuth().getUsername();
            model.addAttribute("user", userService.getUserAuth());
            return "index";
        }
        if (userService.findUserByUsername(username.get()) == null) {
            return "error-404";
        }
        User user = userService.getUserAuth();
        model.addAttribute("user", userService.findUserByUsername(username.get()));
        return "index";

    }
}