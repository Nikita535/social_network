package sosal_network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sosal_network.entity.ProfileInfo;
import sosal_network.entity.User;
import sosal_network.repository.ProfileInfoRepository;
import sosal_network.service.UserService;

import java.io.IOException;
import java.util.Optional;

@Controller
public class ProfileEditController {

    @Autowired
    UserService userService;

    @Autowired
    ProfileInfoRepository profileInfoService;

    /**
     * Get контроллер для страницы редактирования профиля пользователя
     * param model - модель для добавления атрибутов на текущую страницу
     * author - Nekit
     **/
    @GetMapping("/edit")
    public String getEditProfile(Model model) {
        User userFromSession = userService.getUserAuth();
        model.addAttribute("editedProfileInfo", new ProfileInfo());
        model.addAttribute("user", userFromSession);
        model.addAttribute("profileInfo", userService.findByUser_Username(userFromSession.getUsername()));
        return "profileEdit";
    }

    @PostMapping("/edit")
    public String getHome(Model model, @ModelAttribute("editedProfile")ProfileInfo editedProfile,
                          RedirectAttributes redirectAttributes,
                          @RequestParam("currentPassword") String currentPassword,
                          @RequestParam("newPassword") String newPassword,
                          @RequestParam("passwordConfirm") String passwordConfirm,
                          @RequestParam("file")MultipartFile file) throws IOException {

        return userService.editProfile(editedProfile, redirectAttributes, currentPassword, newPassword, passwordConfirm,file);
    }

}
