package com.jobsphere.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="course_progress",
       uniqueConstraints=@UniqueConstraint(name="uk_course_progress_user_course", columnNames={"user_id","course_id"}))
public class CourseProgress {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;
    @Column(name="user_id",nullable=false) public Long userId;
    @Column(name="course_id",nullable=false) public Long courseId;
    @Column(name="completed_days",columnDefinition="TEXT") public String completedDays="[]";
    @Column(name="updated_at",nullable=false) public LocalDateTime updatedAt;
}
