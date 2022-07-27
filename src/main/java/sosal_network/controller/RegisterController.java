package sosal_network.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sosal_network.entity.User;
import sosal_network.service.EmailService;
import sosal_network.service.UserService;

import javax.validation.Valid;

@RequiredArgsConstructor
@Slf4j
@Controller
public class RegisterController {

    @Autowired
    private UserService userService;


    @GetMapping("/register")
    public String getForm(Model model){
        User user = new User();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String registerSave(@ModelAttribute("user") @Valid User user,
                               Model model, RedirectAttributes redirectAttributes) {
        return userService.validateRegister(user, model,redirectAttributes);
    }

    @GetMapping("/activate/{username}")
    public String activate(Model model, @PathVariable String username){
        userService.activateUser(username);
        return "redirect:/login";
    }

}
