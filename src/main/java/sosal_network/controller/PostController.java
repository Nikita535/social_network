package sosal_network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import sosal_network.entity.Post;
import sosal_network.entity.User;
import sosal_network.repository.BanRepository;
import sosal_network.service.ImageService;
import sosal_network.service.PostService;
import sosal_network.service.UserService;

import java.util.List;

@Controller
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    @Autowired
    BanRepository banRepository;

    @Autowired
    ImageService imageService;
    @MessageMapping("/post.send/{username}")
    @SendTo("/topic/post/{username}")
    public Post addPost(@Payload final Post post) {
        postService.savePost(post);
        return post;
    }

    @MessageMapping("/post.delete/{username}")
    @SendTo("/topic/postDelete/{username}")
    public long deletePost(@Payload final long id) {
        postService.deletePostById(id);
        return id;
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
