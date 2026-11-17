package com.jobsphere.repository;

import com.jobsphere.entity.CourseAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseAttemptRepository
        extends JpaRepository<CourseAttempt, Long> {

    List<CourseAttempt>
    findByUserIdOrderByCompletedAtDesc(Long userId);

    List<CourseAttempt>
    findByCourseIdOrderByCompletedAtDesc(Long courseId);

    Optional<CourseAttempt>
    findTopByUserIdAndCourseIdOrderByCompletedAtDesc(
            Long userId,
            Long courseId
    );
}