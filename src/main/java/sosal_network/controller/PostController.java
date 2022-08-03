package sosal_network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sosal_network.entity.Post;
import sosal_network.entity.User;
import sosal_network.repository.PostRepository;
import sosal_network.service.PostService;

import java.time.LocalDateTime;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/post")
        public String addPost(@ModelAttribute("post") Post post, @AuthenticationPrincipal User user){
            postService.savePost(post,user);
            return "redirect:/";
        }


}
