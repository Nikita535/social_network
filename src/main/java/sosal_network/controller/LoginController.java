package sosal_network.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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