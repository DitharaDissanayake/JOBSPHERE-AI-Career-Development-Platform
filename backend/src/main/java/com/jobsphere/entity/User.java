package com.jobsphere.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="users")
public class User { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @Column(name="full_name",nullable=false,length=120) public String fullName; @Column(nullable=false,unique=true,length=160) public String email; @Column(name="password_hash",nullable=false) public String passwordHash; @Enumerated(EnumType.STRING) @Column(nullable=false,length=10) public Role role=Role.USER; @Column(nullable=false) public Boolean active=true; @Column(name="created_at",nullable=false) public LocalDateTime createdAt; public enum Role{USER,ADMIN} }
