package com.jobsphere.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="feedback")
public class Feedback { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @Column(name="user_id") public Long userId; @Column(nullable=false) public Integer rating; @Column(length=1000) public String comment; @Column(name="created_at",nullable=false) public LocalDateTime createdAt; }
