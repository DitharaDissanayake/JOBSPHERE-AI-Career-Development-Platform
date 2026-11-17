package com.jobsphere.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="chat_messages")
public class ChatMessage {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(name="conversation_id",nullable=false) public Long conversationId;
 @Column(name="user_id",nullable=false) public Long userId;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=10) public Role role;
 @Column(nullable=false,columnDefinition="LONGTEXT") public String content;
 @Column(name="created_at",nullable=false) public LocalDateTime createdAt;
 public enum Role{USER,ASSISTANT}
}