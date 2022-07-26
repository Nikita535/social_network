package sosal_network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sosal_network.service.UserService;

@Controller
public class LoginController {


    @GetMapping("/index")
    public String getHome(){
        return "index";
    }

    @GetMapping("/login")
    public String getLogin() throws Exception {
        return "login";
    }





}