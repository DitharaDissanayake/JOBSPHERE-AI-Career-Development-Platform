package com.jobsphere.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="password_reset_tokens")
public class PasswordResetToken {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(name="user_id",nullable=false) public Long userId;
 @Column(name="code_hash",nullable=false,length=255) public String codeHash;
 @Column(name="expires_at",nullable=false) public LocalDateTime expiresAt;
 @Column(nullable=false) public Boolean used=false;
 @Column(nullable=false) public Integer attempts=0;
 @Column(name="created_at",nullable=false) public LocalDateTime createdAt;
}
