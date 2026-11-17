package com.jobsphere.repository;
import com.jobsphere.entity.Course; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List; import java.util.Optional;
public interface CourseRepository extends JpaRepository<Course,Long>{ List<Course> findByActiveTrueOrderByNameAsc(); Optional<Course> findByNameIgnoreCase(String name); }