package sosal_network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sosal_network.entity.Friend;
import sosal_network.entity.ProfileInfo;
import sosal_network.service.FriendService;
import sosal_network.service.UserService;

import java.util.Optional;
import java.util.Set;

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

    @Autowired
    private FriendService friendService;

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
    @GetMapping("/user/{username}")
    public String getHome(@PathVariable Optional<String> username, Model model) {
        if (userService.getUserAuth() == null && (username.isEmpty())) {
            return "/error";
        }
        model.addAttribute("friendService", friendService);
        if (username.isEmpty()) {
            model.addAttribute("user", userService.getUserAuth());
            model.addAttribute("profileInfo", userService.findByUser_Username(userService.getUserAuth().getUsername()));
            return "index";
        }
        if (userService.findUserByUsername(username.get()) == null) {
            return "error-404";
        }
        Set<Friend> friends = userService.getUserAuth().getFriendsList();
        friendService.isFriend(username.get());
        ProfileInfo profileInfo = userService.findByUser_Username(username.get());
        model.addAttribute("user", userService.findUserByUsername(username.get()));
        model.addAttribute("profileInfo", userService.findByUser_Username(username.get()));


        return "index";

    }

    @GetMapping("/user/{username}/friend")
    public String addFriend(@PathVariable Optional<String> username, Model model) {
        if (userService.getUserAuth() == null && (username.isEmpty())) {
            return "/error";
        }
        return friendService.addFriend(username.get());
    }

    @GetMapping("/user/{username}/unfriend")
    public String deleteFriend(@PathVariable Optional<String> username, Model model) {
        if (userService.getUserAuth() == null && (username.isEmpty())) {
            return "/error";
        }
        return friendService.deleteFriend(username.get());
    }
}
