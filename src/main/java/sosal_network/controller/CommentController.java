package sosal_network.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import sosal_network.entity.Comment;
import sosal_network.service.CommentService;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;


    @MessageMapping("/comment.send/{username}")
    @SendTo("/topic/post/comment/{username}")
    public Comment sendMessage(@Payload final Comment comment) {
        commentService.save(comment);
        return comment;
    }

}
