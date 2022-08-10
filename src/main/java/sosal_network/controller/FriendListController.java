package sosal_network.controller;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import java.util.*;

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

    @RequestMapping(value = "/user/{username}/reloadFriendList/{page}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> processReloadData(@RequestBody String body,
                                                    @PathVariable Optional<String> username,
                                                    @PathVariable Optional<String> page) {

        int sizeOfPage = 10;
        JSONObject request = new JSONObject(body);
        String searchLine = friendService.clearSearchLine(request.getString("searchLine"));

        JSONObject response = new JSONObject();

        response = friendService.generateModelOfFriendList(response, username.get(), searchLine, page.get(), sizeOfPage);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(response.toString(),
                headers, HttpStatus.OK);
    }
}
