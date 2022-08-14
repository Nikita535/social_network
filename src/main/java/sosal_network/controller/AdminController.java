package sosal_network.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sosal_network.Enum.Role;
import sosal_network.entity.User;
import sosal_network.service.UserService;

import javax.mail.MessagingException;

@RequiredArgsConstructor
@Slf4j
@Controller
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {


    @Autowired
    private UserService userService;


    @GetMapping("/admin")
    public String admin(Model model){
        model.addAttribute("users",userService.showAllUser());
        model.addAttribute("userService",userService);
        model.addAttribute("ADMIN",Role.ROLE_ADMIN);
        return "admin";
    }

    @GetMapping("/ban_{username}")
    public String ban(@PathVariable String username, Model model) throws MessagingException {
        User user = userService.findUserByUsername(username);
        if(!user.getRoles().contains(Role.ROLE_ADMIN)) {
            user.setBanStatus(true);
            userService.saveUser(user);
        }
        return "redirect:/admin";
    }

    @GetMapping("/unban_{username}")
    public String unban(@PathVariable String username) throws MessagingException {
        User user = userService.findUserByUsername(username);
        user.setBanStatus(false);
        userService.saveUser(user);
        return "redirect:/admin";
    }
}