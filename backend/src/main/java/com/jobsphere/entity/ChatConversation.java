package com.jobsphere.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="chat_conversations")
public class ChatConversation {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(name="user_id",nullable=false) public Long userId;
 @Column(name="user_email",nullable=false,length=160) public String userEmail;
 @Column(nullable=false,length=180) public String title;
 @Column(name="created_at",nullable=false) public LocalDateTime createdAt;
 @Column(name="updated_at",nullable=false) public LocalDateTime updatedAt;
}