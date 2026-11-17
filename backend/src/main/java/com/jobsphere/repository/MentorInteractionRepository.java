package com.jobsphere.repository;

import com.jobsphere.entity.MentorInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MentorInteractionRepository extends JpaRepository<MentorInteraction,Long> {
    List<MentorInteraction> findByUserIdOrderByCreatedAtDesc(Long userId);
}
