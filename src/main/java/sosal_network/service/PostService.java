package sosal_network.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sosal_network.entity.Post;
import sosal_network.entity.User;
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
    PostRepository postRepository;

    public void savePost(Post post, User user){
        post.setDateOfCreate(LocalDateTime.now());
        post.setUser(user);
        postRepository.save(post);
    }

    public List<Post> showPost(User user){
       return postRepository.findPostsByUser(user).stream().sorted(Comparator.comparing(Post::getDateOfCreate).reversed()).collect(Collectors.toList());
    }

    public String showTimeAgo(Post post){
        long Seconds = ChronoUnit.SECONDS.between(post.getDateOfCreate(),LocalDateTime.now());
        long Minutes = ChronoUnit.MINUTES.between(post.getDateOfCreate(),LocalDateTime.now());
        long Hours = ChronoUnit.HOURS.between(post.getDateOfCreate(),LocalDateTime.now());
        long Days = ChronoUnit.DAYS.between(post.getDateOfCreate(),LocalDateTime.now());
        long Months = ChronoUnit.MONTHS.between(post.getDateOfCreate(),LocalDateTime.now());

        if (Seconds<60){
            return Seconds%10!=0 && Seconds%10 < 5 ? Seconds+" секунды назад" : Seconds+" секунд назад" ;
        }

        if(Minutes>=1 && Minutes<60){
            return Minutes%10!=0 && Minutes%10< 5 ? Minutes+" минуты назад" : Minutes+" минут назад" ;
        }
        if(Hours>=1 && Hours<24){
            return  Hours%10!=0 && Hours%10 < 5 ? Hours+" часа назад" : Hours+" часов назад" ;
        }
        if(Days >=1 && Days<30){
            return  Days%10!=0 && Days%10 < 5 ? Days+" дня назад" : Days+" дней назад" ;
        }
        if(Months>=1 && Months<12){
            return  Months%10!=0 && Months%10 < 5 ? Months+" месяца назад" : Months+" месяцев назад" ;
        }
        return " давно";
    }

}
