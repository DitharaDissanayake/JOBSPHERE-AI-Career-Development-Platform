package com.jobsphere.repository;
import com.jobsphere.entity.MediaRecommendation; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface MediaRecommendationRepository extends JpaRepository<MediaRecommendation,Long>{ List<MediaRecommendation> findTop12ByUserIdOrderByCreatedAtDesc(Long userId); }
