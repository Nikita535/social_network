package sosal_network.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sosal_network.entity.Comment;
import sosal_network.entity.Post;
import sosal_network.repository.CommentRepository;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;


    public void save(Comment comment){
        commentRepository.save(comment);
    }

    public List<Comment> findCommentByPost(Post post){
        return commentRepository.findCommentByPost(post);
    }
}
