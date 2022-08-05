package sosal_network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sosal_network.entity.ProfileInfo;
import sosal_network.entity.User;
import sosal_network.repository.ProfileInfoRepository;
import sosal_network.service.ImageService;
import sosal_network.service.UserService;

import java.io.IOException;
import java.util.Optional;

@Controller
public class ProfileEditController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileInfoRepository profileInfoService;

    @Autowired
    private ImageService imageService;

    /**
     * Get контроллер для страницы редактирования профиля пользователя
     * param model - модель для добавления атрибутов на текущую страницу
     * author - Renat
     **/
    @GetMapping("/edit")
    public String getEditProfile(Model model, @AuthenticationPrincipal User userFromSession) {
        model.addAttribute("editedProfileInfo", new ProfileInfo());
        model.addAttribute("user", userFromSession);
        model.addAttribute("profileInfo", userService.findByUser_Username(userFromSession.getUsername()));
        model.addAttribute("avatar",imageService.findImageByUserAndIsPreview( userFromSession,true));
        return "profileEdit";
    }

    /**
     * Get контроллер для страницы редактирования профиля пользователя
     * param model - модель для добавления атрибутов на текущую страницу
     * author - Renat
     **/
    @PostMapping("/edit/profile")
    public String changeProfileInfo(RedirectAttributes redirectAttributes, @ModelAttribute("editedProfile") ProfileInfo editedProfile,
                                    @RequestParam("profileDateOfBirth") String dateOfBirth,
                                    @AuthenticationPrincipal User user){

        return userService.editProfile(editedProfile, dateOfBirth, redirectAttributes, user);
    }

    @PostMapping("/edit/photo")
    public String changePhoto(RedirectAttributes redirectAttributes, @RequestParam("file") MultipartFile file,
                              @AuthenticationPrincipal User user) throws IOException {
        return userService.changePhoto(redirectAttributes, file, user);
    }


    @PostMapping("/edit/password")
    public String changePassword(RedirectAttributes redirectAttributes,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("passwordConfirm") String passwordConfirm, @AuthenticationPrincipal User user) {
        return userService.changePassword(user, currentPassword, newPassword, passwordConfirm, redirectAttributes);
    }


}
