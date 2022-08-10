package sosal_network.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sosal_network.entity.ChatMessage;
import sosal_network.entity.User;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findChatMessagesByUserFromAndUserTo(User userFrom, User userTo);
}
