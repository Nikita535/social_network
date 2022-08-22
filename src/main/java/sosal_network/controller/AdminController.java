package sosal_network.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sosal_network.Enum.BanStatus;
import sosal_network.Enum.Role;
import sosal_network.entity.User;
import sosal_network.repository.BanRepository;
import sosal_network.service.AdminService;
import sosal_network.service.UserService;

import javax.mail.MessagingException;

@RequiredArgsConstructor
@Slf4j
@Controller
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {


    @Autowired
    private UserService userService;

    @Autowired
    AdminService adminService;

    @Autowired
    BanRepository banRepository;

    @GetMapping("/admin")
    public String admin(Model model){
        model.addAttribute("users",userService.showAllUser());
        model.addAttribute("userService",userService);
        model.addAttribute("ADMIN",Role.ROLE_ADMIN);
        model.addAttribute("banRepository",banRepository);
        return "admin";
    }

    @GetMapping("/ban/{username}/{banTime}")
    public String ban(@PathVariable String username,@PathVariable String banTime, Model model) throws MessagingException {
        adminService.checkBanTime(banTime,username);
        return "redirect:/admin";
    }

    @GetMapping("/unban/{username}")
    public String unban(@PathVariable String username) throws MessagingException {
        adminService.unBanUser(username);
        return "redirect:/admin";
    }
}