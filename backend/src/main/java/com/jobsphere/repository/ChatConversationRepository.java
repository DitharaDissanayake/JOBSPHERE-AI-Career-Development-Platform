package com.jobsphere.repository;
import com.jobsphere.entity.ChatConversation; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ChatConversationRepository extends JpaRepository<ChatConversation,Long>{
 List<ChatConversation> findByUserIdOrderByUpdatedAtDesc(Long userId);
 Optional<ChatConversation> findByIdAndUserId(Long id,Long userId);
}