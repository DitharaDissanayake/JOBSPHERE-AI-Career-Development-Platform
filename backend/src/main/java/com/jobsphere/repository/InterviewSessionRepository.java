package com.jobsphere.repository;
import com.jobsphere.entity.InterviewSession; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface InterviewSessionRepository extends JpaRepository<InterviewSession,Long>{ List<InterviewSession> findTop10ByUserIdOrderByCreatedAtDesc(Long userId); }
