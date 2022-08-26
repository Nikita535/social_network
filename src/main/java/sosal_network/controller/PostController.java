package sosal_network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sosal_network.entity.*;
import sosal_network.repository.BanRepository;
import sosal_network.service.ImageService;
import sosal_network.service.PostService;
import sosal_network.service.UserService;

import java.io.IOException;
import java.util.*;

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

    @RequestMapping(value = "/post/create", method = RequestMethod.POST)
    @ResponseBody
    public List<Image> processReloadData(@RequestParam("file") List<MultipartFile> files) {
        return files.stream().map(file -> {
            try {
                Image image = imageService.toImageEntity(file);
                imageService.save(image);
                return image;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
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
