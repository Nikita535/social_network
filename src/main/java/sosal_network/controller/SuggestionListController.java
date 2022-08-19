package sosal_network.controller;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sosal_network.entity.User;
import sosal_network.service.FriendService;
import sosal_network.service.ImageService;
import sosal_network.service.UserService;

import java.util.*;

@Controller
public class SuggestionListController {

    @Autowired
    private FriendService friendService;
    @Autowired
    private UserService userService;
    @Autowired
    private ImageService imageService;

    @GetMapping("/user/{username}/suggestion/{page}")
    public String getFriendList(Model model, @PathVariable Optional<String> username,
                                @PathVariable Optional<String> page,
                                @ModelAttribute("searchLine") String searchLine, @AuthenticationPrincipal User user){
        if (userService.findUserByUsername(username.get()).isBanStatus()) {
            return "banError";
        }

        if (username.get().isEmpty()) {
            return "/error";
        }

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
        return "suggestionList";
    }

    @RequestMapping(value = "/user/{username}/reloadSuggestionList/{page}", method = RequestMethod.POST)
    @ResponseBody
    public List<Object> processReloadData(@RequestBody String body,
                                          @PathVariable Optional<String> username,
                                          @PathVariable Optional<Integer> page) {

        JSONObject request = new JSONObject(body);
        String searchLine = friendService.clearSearchLine(request.getString("searchLine")).
                replaceAll("[\s]{2,}", " ").trim();

        return friendService.findSuggestions(username.get(),
                searchLine, page.get());
    }
}