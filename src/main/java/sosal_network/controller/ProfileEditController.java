package sosal_network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sosal_network.entity.ProfileInfo;
import sosal_network.entity.User;
import sosal_network.repository.BanRepository;
import sosal_network.service.UserService;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
public class ProfileEditController {

    @Autowired
    private UserService userService;

    @Autowired
    BanRepository banRepository;

    /**
     * Get контроллер для страницы редактирования профиля пользователя
     * param model - модель для добавления атрибутов на текущую страницу
     * author - Renat
     **/
    @GetMapping("/edit")
    public String getEditProfile(Model model, @AuthenticationPrincipal User userFromSession) {

        if (banRepository.findBanInfoById(userFromSession.getBanInfo().getId()).isBanStatus()) {
            return "banError";
        }

        model.addAttribute("editedProfileInfo", new ProfileInfo());
        model.addAttribute("user", userService.findUserByUsername(userFromSession.getUsername()));
        return "profileEdit";
    }

    /**
     * Get контроллер для страницы редактирования профиля пользователя
     * param model - модель для добавления атрибутов на текущую страницу
     * author - Renat
     **/
    @PostMapping("/edit/profile")
    public String changeProfileInfo(RedirectAttributes redirectAttributes, @ModelAttribute("editedProfileInfo") @Valid ProfileInfo editedProfile, BindingResult bindingResult, Model model,

                                    @AuthenticationPrincipal User user) {
        if (editedProfile.getDateOfBirth() != null) {
            if (ChronoUnit.YEARS.between(editedProfile.getDateOfBirth(), LocalDate.now()) < 14) {
                bindingResult.addError(new FieldError("profileInfo", "dateOfBirth", "Вы не можете указать возраст меньше 14-ти лет"));
            }
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userService.findUserByUsername(user.getUsername()));
            return "profileEdit";
        }
        return userService.editProfile(editedProfile, redirectAttributes, userService.findUserByUsername(user.getUsername()));
    }

    @PostMapping("/edit/photo")
    public String changePhoto(RedirectAttributes redirectAttributes, @RequestParam("file") MultipartFile file,
                              @AuthenticationPrincipal User user) throws IOException {
        return userService.changePhoto(redirectAttributes, file, userService.findUserByUsername(user.getUsername()));
    }


    @PostMapping("/edit/password")
    public String changePassword(RedirectAttributes redirectAttributes,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("passwordConfirm") String passwordConfirm, @AuthenticationPrincipal User user) {
        return userService.changePassword(user, currentPassword, newPassword, passwordConfirm, redirectAttributes);
    }


}
