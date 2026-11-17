package com.jobsphere.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="interview_sessions")
public class InterviewSession {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(name="user_id",nullable=false) public Long userId;
 @Column(length=120) public String category;
 @Column(length=160) public String position;
 @Column(name="source_type",length=30,nullable=false) public String sourceType;
 @Column(name="session_json",nullable=false,columnDefinition="LONGTEXT") public String sessionJson;
 @Column(name="created_at",nullable=false) public LocalDateTime createdAt;
}
