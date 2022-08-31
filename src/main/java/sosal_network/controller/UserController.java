package sosal_network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import sosal_network.Enum.Role;
import sosal_network.entity.Post;
import sosal_network.entity.User;
import sosal_network.exception.BadRequestException;
import sosal_network.exception.UserWasBanedException;
import sosal_network.repository.BanRepository;
import sosal_network.service.AdminService;
import sosal_network.service.FriendService;
import sosal_network.service.PostService;
import sosal_network.service.UserService;

import java.util.List;
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
    private FriendService friendService;

    @Autowired
    private BanRepository banRepository;

    @Autowired
    AdminService adminService;


    /**
     * Get контроллер для перенаправления пользователя на страницу профиля после авторизации
     * author - Nekit
     **/
    @GetMapping({"/", "/user"})
    public String getUser(@AuthenticationPrincipal User authenticatedUser) {
        return "redirect:/user/" + authenticatedUser.getUsername();
    }

    /**
     * Get контроллер для страницы профиля пользователя
     * param username - имя пользователя
     * param model - модель для добавления атрибутов на текущую страницу
     * author - Nekit
     **/
    @GetMapping("/user/{username}")
    public String getHome(@PathVariable Optional<String> username, Model model, @AuthenticationPrincipal User authenticatedUser) {

        try {
            if (authenticatedUser == null && (username.isEmpty())) {
                throw new BadRequestException();
            }
            userService.findUserByUsername(username.get());
        } catch (Exception e) {
            throw new BadRequestException("username = null", e);
        }

        if (banRepository.findBanInfoById(authenticatedUser.getBanInfo().getId()).isBanStatus()) {
            throw new UserWasBanedException("user was banned");
        }
        List<User> possibleFriends = userService.findPossibleFriendsByMutualFriends(authenticatedUser);

        model.addAttribute("possibleFriends", possibleFriends);
        model.addAttribute("quantityOfMutualFriends", userService.findMutualFriends(authenticatedUser.getId(), possibleFriends));
        model.addAttribute("postService", postService);
        model.addAttribute("user", userService.findUserByUsername(username.get()));
        model.addAttribute("currentUser", userService.findUserByUsername(authenticatedUser.getUsername()));
        model.addAttribute("friends", friendService.getAcceptedFriends(username.get()));
        model.addAttribute("isFriend", friendService.isFriends(username.get()));
        model.addAttribute("friendAccepted", friendService.checkFriendStatus(username.get()));
        model.addAttribute("isInviteReceived", friendService.isInviteReceived(username.get()));
        model.addAttribute("isInviteSend", friendService.isInviteSend(username.get()));
        model.addAttribute("post", new Post());
        model.addAttribute("ADMIN", Role.ROLE_ADMIN);
        return "index";

    }

    @GetMapping("/user/{username}/friend")
    public String sendInvite(@PathVariable Optional<String> username, Model model, @AuthenticationPrincipal User authenticatedUser,
                             @RequestParam(value = "where", required = false) String where) {
        if (authenticatedUser == null && (username.isEmpty()) || Objects.equals(userService.
                findUserByUsername(username.get()).getUsername(), userService.getUserAuth().getUsername())) {
            throw new BadRequestException();
        }

        if (banRepository.findBanInfoById(authenticatedUser.getBanInfo().getId()).isBanStatus()) {
            throw new UserWasBanedException();
        }
        return friendService.sendInvite(username.get(), where);
    }

    @GetMapping("/user/{username}/friend/{result}")
    public String acceptInvite(@PathVariable Optional<String> username, @AuthenticationPrincipal User authenticatedUser, @PathVariable int result,
                               @RequestParam(value = "where", required = false) String where) {
        if (authenticatedUser == null && (username.isEmpty()) || (result > 2) || Objects.equals(
                userService.findUserByUsername(username.get()).getUsername(), userService.getUserAuth().getUsername())) {
            throw new BadRequestException();
        }
        return friendService.resultInvite(username.get(), result, where);
    }

    @GetMapping("/user/{username}/unfriend")
    public String deleteFriend(@PathVariable Optional<String> username, @AuthenticationPrincipal User authenticatedUser,
                               @RequestParam(value = "where", required = false) String where) {
        if (authenticatedUser == null && (username.isEmpty()) || Objects.equals(userService.
                findUserByUsername(username.get()).getUsername(), userService.getUserAuth().getUsername())) {
            throw new BadRequestException();
        }

        if (banRepository.findBanInfoById(Objects.requireNonNull(authenticatedUser).getBanInfo().getId()).isBanStatus()) {
            throw new UserWasBanedException();
        }
        return friendService.deleteFriend(username.get(), where);
    }
}
