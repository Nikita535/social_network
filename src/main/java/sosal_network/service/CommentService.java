package sosal_network.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sosal_network.entity.Comment;
import sosal_network.entity.Post;
import sosal_network.repository.CommentRepository;

import java.awt.print.Pageable;
import java.util.List;

@Service
public class CommentService {
    private static final int COMMENT_PAGE_SIZE = 3;

    @Autowired
    CommentRepository commentRepository;


    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    public List<Comment> findCommentsByPostOrderByTime(Post post, int page) {
        return commentRepository.findCommentsByPostOrderByTime(post,PageRequest.of(page,COMMENT_PAGE_SIZE) );
    }

}
