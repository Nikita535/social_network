package sosal_network.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sosal_network.entity.Comment;
import sosal_network.repository.CommentRepository;

@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;


    public void save(Comment comment) {
        commentRepository.save(comment);
    }

}
