package sosal_network.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sosal_network.entity.ProfileInfo;
import sosal_network.entity.User;
import sosal_network.service.FriendService;
import sosal_network.service.ImageService;
import sosal_network.service.UserService;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class FriendListController {

    @Autowired
    private FriendService friendService;
    @Autowired
    private UserService userService;
    @Autowired
    private ImageService imageService;

    @GetMapping("/user/{username}/friendList/{page}")
    public String getFriendList(Model model, @PathVariable Optional<String> username,
                                @PathVariable Optional<String> page,
                                @ModelAttribute("searchLine") String searchLine, @AuthenticationPrincipal User user){


        if (username.isEmpty()) {
            return "/error";
        }
        int sizeOfPage = 10;
        model = friendService.generateModelOfFriendList(model, username.get(), searchLine, page.get(), sizeOfPage);

        model.addAttribute("isAdminOfTheFriendList", Objects.equals(userService.findUserByUsername(username.get()).getUsername(), user.getUsername()));
        model.addAttribute("searchLine", searchLine);
        model.addAttribute("friendService", friendService);
        model.addAttribute("user", userService.findUserByUsername(username.get()));
        model.addAttribute("profileInfo", userService.findProfileInfoByUser(user));
        model.addAttribute("friends",friendService.getAcceptedFriends(username.get()));
        model.addAttribute("isFriend",friendService.isFriends(username.get()));
        model.addAttribute("friendAccepted",friendService.checkFriendStatus(username.get()));
        model.addAttribute("isInviteRecieved",friendService.isInviteRecieved(username.get()));
        model.addAttribute("isInviteSend",friendService.isInviteSend(username.get()));
        model.addAttribute("imageService",imageService);
        return "friendList";
    }

    @PostMapping("/user/{username}/search")
    public String findFriendList(Model model, @PathVariable Optional<String> username,
                                 @RequestParam("searchLine") String searchLine,
                                 RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("searchLine", friendService.clearSearchLine(searchLine));
        return "redirect:/user/" + username.get() + "/friendList/1";
    }
}
