package sosal_network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sosal_network.entity.Post;
import sosal_network.entity.User;
import sosal_network.service.PostService;
import sosal_network.service.UserService;

import java.util.List;

@Controller
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;


    @PostMapping("/post")
    public String addPost(@RequestParam("imageList") List<MultipartFile> files,@ModelAttribute("post") Post post,   @AuthenticationPrincipal User user) {
        if (user.isBanStatus()) {
            return "banError";
        }

        postService.savePost(files,post, user);
        return "redirect:/";
    }


    @GetMapping("/post/{username}/{page}")
    @ResponseBody
    public List<Post> showPosts(@PathVariable String username, @PathVariable int page)
    {
        User user=userService.findUserByUsername(username);
        return postService.showLastPosts(user,page);
    }

    @GetMapping("/post/{idPost}")
    @ResponseBody
    public Post showPosts(@PathVariable Long idPost)
    {
        return postService.findPostById(idPost);
    }

}
