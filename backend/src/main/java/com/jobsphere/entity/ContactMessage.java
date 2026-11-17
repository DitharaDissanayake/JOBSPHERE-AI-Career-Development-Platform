package com.jobsphere.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 120)
    public String name;

    @Column(nullable = false, length = 160)
    public String email;

    @Column(length = 255)
    public String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    public String message;

    @Column(name = "is_read", nullable = false)
    public Boolean read = false;

    @Column(name = "replied", nullable = false)
    public Boolean replied = false;

    @Column(name = "admin_reply", columnDefinition = "TEXT")
    public String adminReply;

    @Column(name = "replied_at")
    public LocalDateTime repliedAt;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;
}