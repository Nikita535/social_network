package sosal_network.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import sosal_network.entity.Comment;
import sosal_network.service.CommentService;
import sosal_network.service.PostService;

import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;


    @MessageMapping("/comment.send/{username}")
    @SendTo("/topic/post/comment/{username}")
    public Comment sendMessage(@Payload final Comment comment) {

        commentService.save(comment);
        return comment;
    }

    @GetMapping("/{postId}/comment/{page}")
    @ResponseBody
    List<Comment> loadComments(@PathVariable long postId,@PathVariable int page)
    {
        return commentService.findCommentsByPostOrderByTime( postService.findPostById(postId),page);
    }

}
