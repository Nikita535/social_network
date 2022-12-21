package sosal_network.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sosal_network.entity.ChatMessage;
import sosal_network.entity.User;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Long> {


    @Query(value = "select * from jpa.chat_message  where (user_from_id = ?1 and user_to_id = ?2) or (user_from_id = ?2 and user_to_id = ?1) order by time desc", nativeQuery = true)
    List<ChatMessage> findChatMessagesByUserFromAndUserTo(User userFrom, User userTo, Pageable pageable);

    ChatMessage findChatMessagesById(Long id);
}
