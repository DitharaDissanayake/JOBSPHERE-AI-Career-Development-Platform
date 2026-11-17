package com.jobsphere.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="career_analyses")
public class CareerAnalysis { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @Column(name="user_id",nullable=false) public Long userId; @Column(name="profile_snapshot",nullable=false,columnDefinition="LONGTEXT") public String profileSnapshot; @Column(name="analysis_json",nullable=false,columnDefinition="LONGTEXT") public String analysisJson; @Column(name="created_at",nullable=false) public LocalDateTime createdAt; }
