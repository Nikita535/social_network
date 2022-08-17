package sosal_network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sosal_network.Enum.InviteStatus;
import sosal_network.entity.Post;
import sosal_network.entity.User;
import sosal_network.service.FriendService;
import sosal_network.service.ImageService;
import sosal_network.service.PostService;
import sosal_network.service.UserService;

import java.util.Objects;
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
    PostService postService;
    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private FriendService friendService;

    /**
     * Get контроллер для перенаправления пользователя на страницу профиля после авторизации
     * author - Nekit
     **/
    @GetMapping("/")
    public String getUser(@AuthenticationPrincipal User authentificatedUser) {
        return "redirect:/user/" + authentificatedUser.getUsername();
    }

    /**
     * Get контроллер для страницы профиля пользователя
     * param username - имя пользователя
     * param model - модель для добавления атрибутов на текущую страницу
     * author - Nekit
     **/
    @GetMapping("/user/{username}")
    public String getHome(@PathVariable Optional<String> username, Model model, @AuthenticationPrincipal User authentificatedUser) {
        if (authentificatedUser == null && (username.isEmpty())) {
            return "/error";
        }
        model.addAttribute("postService", postService);
        if (userService.findUserByUsername(username.get()) == null) {
            return "error-404";
        }
        User currentUser=userService.findUserByUsername(username.get());
        model.addAttribute("user", userService.findUserByUsername(username.get()));
        model.addAttribute("profileInfo", userService.findProfileInfoByUser(currentUser));
        model.addAttribute("friends",friendService.getAcceptedFriends(username.get()));
        model.addAttribute("isFriend",friendService.isFriends(username.get()));
        model.addAttribute("friendAccepted",friendService.checkFriendStatus(username.get()));
        model.addAttribute("isInviteRecieved",friendService.isInviteRecieved(username.get()));
        model.addAttribute("isInviteSend",friendService.isInviteSend(username.get()));
        model.addAttribute("post",new Post());
        model.addAttribute("posts",postService.showLastPosts(currentUser,0));
        return "index";

    }

    @GetMapping("/user/{username}/friend")
    public String sendInvite(@PathVariable Optional<String> username, Model model, @AuthenticationPrincipal User authentificatedUser,
                             @RequestParam(value = "where", required = false) String where) {
        if (authentificatedUser == null && (username.isEmpty()) || Objects.equals(userService.findUserByUsername(username.get()).getUsername(), userService.getUserAuth().getUsername())) {
            return "/error";
        }
        return friendService.sendInvite(username.get(), where);
    }

    @GetMapping("/user/{username}/friend/{result}")
    public String acceptInvite(@PathVariable Optional<String> username, Model model, @AuthenticationPrincipal User authentificatedUser,@PathVariable int result,
                               @RequestParam(value = "where", required = false) String where)
    {
        if (authentificatedUser == null && (username.isEmpty())||(result>2) || Objects.equals(userService.findUserByUsername(username.get()).getUsername(), userService.getUserAuth().getUsername())) {
            return "/error";
        }
        return friendService.resultInvite(username.get(), result, where);
    }
    @GetMapping("/user/{username}/unfriend")
    public String deleteFriend(@PathVariable Optional<String> username, Model model, @AuthenticationPrincipal User authentificatedUser,
                               @RequestParam(value="where", required = false) String where) {
        if (authentificatedUser == null && (username.isEmpty()) || Objects.equals(userService.findUserByUsername(username.get()).getUsername(), userService.getUserAuth().getUsername())) {
            return "/error";
        }
        return friendService.deleteFriend(username.get(), where);
    }
}
