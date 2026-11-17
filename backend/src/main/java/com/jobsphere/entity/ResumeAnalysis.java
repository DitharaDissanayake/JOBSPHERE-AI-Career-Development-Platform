package com.jobsphere.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="resume_analyses")
public class ResumeAnalysis { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @Column(name="resume_id",nullable=false) public Long resumeId; @Column(name="user_id",nullable=false) public Long userId; @Column(name="analysis_json",nullable=false,columnDefinition="LONGTEXT") public String analysisJson; @Column(name="created_at",nullable=false) public LocalDateTime createdAt; }
