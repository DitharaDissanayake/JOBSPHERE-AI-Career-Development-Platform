package com.jobsphere.repository;

import com.jobsphere.entity.CourseProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CourseProgressRepository extends JpaRepository<CourseProgress,Long> {
    Optional<CourseProgress> findByUserIdAndCourseId(Long userId, Long courseId);
}
