package sosal_network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sosal_network.entity.User;
import sosal_network.exception.BadRequestException;
import sosal_network.exception.UserWasBanedException;
import sosal_network.repository.BanRepository;
import sosal_network.service.AdminService;

@ControllerAdvice
public class GlobalControllerExceptionResolver {

    @Autowired
    BanRepository banRepository;

    @Autowired
    AdminService adminService;

    @ExceptionHandler(UserWasBanedException.class)
    public String handlerUserWasBanedException(UserWasBanedException e, @AuthenticationPrincipal User authenticatedUser, Model model) {
        model.addAttribute("banRepository", banRepository);
        model.addAttribute("userFromSession",authenticatedUser);
        model.addAttribute("adminService", adminService);
        return "banError";
    }

    @ExceptionHandler(BadRequestException.class)
    public String handlerBadRequestException(BadRequestException e, Model model) {
        return "error-404";
    }

}


