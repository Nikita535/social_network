package sosal_network.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sosal_network.entity.User;
import sosal_network.service.PostService;
import sosal_network.service.UserService;

@Controller
public class LikeController {

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;


    @PostMapping("/postLike/{postId}/like")
    @ResponseBody
    public void setLikeMapping(@PathVariable Long postId, @AuthenticationPrincipal User currentUser) {
        postService.setLike(postId, currentUser);
    }
}
