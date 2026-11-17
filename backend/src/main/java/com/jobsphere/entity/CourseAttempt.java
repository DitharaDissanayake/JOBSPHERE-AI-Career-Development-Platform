package com.jobsphere.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_attempts")
public class CourseAttempt {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 public Long id;

 @Column(name = "course_id", nullable = false)
 public Long courseId;

 @Column(name = "user_id", nullable = false)
 public Long userId;

 @Column(nullable = false)
 public Integer score;

 @Column(name = "correct_answers", nullable = false)
 public Integer correctAnswers;

 @Column(name = "total_questions", nullable = false)
 public Integer totalQuestions;

 @Column(nullable = false)
 public Boolean passed;

 @Column(name = "answers_json", columnDefinition = "LONGTEXT")
 public String answersJson;

 @Column(name = "completed_at", nullable = false)
 public LocalDateTime completedAt;

 @Column(name = "certificate_sent", nullable = false)
 public Boolean certificateSent = false;
}