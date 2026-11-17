package com.jobsphere.repository;
import com.jobsphere.entity.CourseQuestion; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface CourseQuestionRepository extends JpaRepository<CourseQuestion,Long>{ List<CourseQuestion> findByCourseIdAndActiveTrueOrderByIdAsc(Long courseId); }