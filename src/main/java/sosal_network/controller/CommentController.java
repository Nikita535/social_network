package sosal_network.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sosal_network.entity.Comment;
import sosal_network.entity.Image;
import sosal_network.service.CommentService;
import sosal_network.service.ImageService;

import java.io.IOException;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ImageService imageService;


    @MessageMapping("/comment.send/{username}")
    @SendTo("/topic/post/comment/{username}")
    public Comment sendMessage(@Payload final Comment comment) {

        commentService.save(comment);
        return comment;
    }


    @RequestMapping(value = "/message/create", method = RequestMethod.POST)
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

}
