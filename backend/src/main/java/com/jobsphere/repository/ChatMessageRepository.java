package com.jobsphere.repository;
import com.jobsphere.entity.ChatMessage; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long>{
 List<ChatMessage> findByConversationIdAndUserIdOrderByCreatedAtAsc(Long conversationId,Long userId);
}