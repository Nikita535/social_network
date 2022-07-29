package sosal_network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sosal_network.service.UserService;

import java.util.Optional;

//@Controller
//public class ProfileEditController {
//
//    @Autowired
//    UserService userService;
//
//    @GetMapping({"/user/profileEdit"})
//    public String getHome() {
//
//        return "redirect:/profileEdit";
//
//    }
//}
