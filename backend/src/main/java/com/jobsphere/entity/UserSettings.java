package com.jobsphere.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="user_settings")
public class UserSettings {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(name="user_id",nullable=false,unique=true) public Long userId;
 @Column(nullable=false) public Boolean emailNotifications=true;
 @Column(nullable=false) public Boolean courseReminders=true;
 @Column(nullable=false) public Boolean careerRecommendations=true;
 @Column(nullable=false) public Boolean profileVisibility=true;
 @Column(length=20,nullable=false) public String theme="system";
 @Column(length=20,nullable=false) public String language="English";
 @Column(name="updated_at",nullable=false) public LocalDateTime updatedAt;
}