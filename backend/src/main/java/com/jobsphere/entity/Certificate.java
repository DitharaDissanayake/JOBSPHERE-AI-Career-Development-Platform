package com.jobsphere.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="certificates")
public class Certificate { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @Column(name="user_id",nullable=false) public Long userId; @Column(name="file_name",nullable=false) public String fileName; @Column(name="content_type",nullable=false) public String contentType; @Column(name="file_size",nullable=false) public Long fileSize; @Lob @Column(name="file_data",nullable=false,columnDefinition="LONGBLOB") public byte[] fileData; @Column(name="uploaded_at",nullable=false) public LocalDateTime uploadedAt; }
