package sosal_network.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sosal_network.entity.Post;
import sosal_network.entity.PostImage;
import sosal_network.entity.User;
import sosal_network.repository.PostImageRepository;
import sosal_network.repository.PostRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private PostImageRepository postImageRepository;

    public void savePost(List<MultipartFile> files, Post post, User user) {

        post.setDateOfCreate(LocalDateTime.now());
        post.setUser(user);
        postRepository.save(post);
        if (!files.isEmpty() && !files.get(0).isEmpty()) {
            List<PostImage> postImages = imageService.convertPostImages(files, post);
            postImageRepository.saveAll(postImages);
        }
    }

    public List<Post> showPost(User user) {
        return postRepository.findPostsByUser(user).stream().sorted(Comparator.comparing(Post::getDateOfCreate).reversed()).collect(Collectors.toList());
    }

    public List<Post> showLast5Posts(User user)
    {
        return postRepository.findLast5ByUser(user);
    }

    public List<Post> showLastPosts(User user,int page)
    {
        Pageable pageable=PageRequest.of(page,5);
        return postRepository.findAllByUserOrderByIdDesc(user,pageable).stream().peek(p->p.setFromNow(showTimeAgo(p))).collect(Collectors.toList());
    }

    public String showTimeAgo(Post post) {
        long seconds = ChronoUnit.SECONDS.between(post.getDateOfCreate(), LocalDateTime.now());
        long minutes = ChronoUnit.MINUTES.between(post.getDateOfCreate(), LocalDateTime.now());
        long hours = ChronoUnit.HOURS.between(post.getDateOfCreate(), LocalDateTime.now());
        long days = ChronoUnit.DAYS.between(post.getDateOfCreate(), LocalDateTime.now());
        long months = ChronoUnit.MONTHS.between(post.getDateOfCreate(), LocalDateTime.now());

        if (seconds < 60) {
            if (seconds % 10 == 1 && seconds / 10 != 1) {
                return seconds + " секунду назад";
            } else {
                return seconds % 10 != 0 && seconds % 10 < 5 && seconds / 10 != 1 ? seconds + " секунды назад" : seconds + " секунд назад";
            }
        }

        if (minutes >= 1 && minutes < 60) {
            if (minutes % 10 == 1 && minutes / 10 != 1) {
                return minutes + " минуту назад";
            } else {
                return minutes % 10 != 0 && minutes % 10 < 5 && minutes / 10 != 1 ? minutes + " минуты назад" : minutes + " минут назад";
            }
        }
        if (hours >= 1 && hours < 24) {
            if (hours % 10 == 1 && hours / 10 != 1) {
                return hours + " час назад";
            } else {
                return hours % 10 != 0 && hours % 10 < 5 && hours / 10 != 1 ? hours + " часа назад" : hours + " часов назад";
            }
        }
        if (days >= 1 && days < 30) {
            if (days % 10 == 1 && days / 10 != 1) {
                return days + " день назад";
            } else {
                return days % 10 != 0 && days % 10 < 5 && days / 10 != 1 ? days + " дня назад" : days + " день назад";
            }
        }
        if (months >= 1 && months < 12) {
            if (months % 10 == 1 && months / 10 != 1) {
                return months + " месяц назад";
            } else {
                return months % 10 != 0 && months % 10 < 5 && months / 10 != 1 ? months + " месяца назад" : months + " месяцев назад";
            }
        }
        return " давно";
    }


}
