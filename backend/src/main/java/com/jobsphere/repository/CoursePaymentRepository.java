package com.jobsphere.repository;

import com.jobsphere.entity.CoursePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CoursePaymentRepository extends JpaRepository<CoursePayment,Long> {
    Optional<CoursePayment> findTopByUserIdAndCourseIdAndStatusOrderByPaidAtDesc(Long userId, Long courseId, String status);
    Optional<CoursePayment> findByOrderId(String orderId);
    List<CoursePayment> findByUserIdOrderByCreatedAtDesc(Long userId);
}
