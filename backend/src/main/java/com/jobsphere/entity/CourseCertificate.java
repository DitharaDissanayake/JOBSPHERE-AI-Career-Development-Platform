package com.jobsphere.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="course_certificates")
public class CourseCertificate {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(name="course_id",nullable=false) public Long courseId;
 @Column(name="user_id",nullable=false) public Long userId;
 @Column(name="attempt_id",nullable=false) public Long attemptId;
 @Column(nullable=false,length=120) public String certificateNumber;
 @Column(name="file_name",nullable=false) public String fileName;
 @Column(name="content_type",nullable=false) public String contentType;
 @Lob @Column(name="file_data",nullable=false,columnDefinition="LONGBLOB") public byte[] fileData;
 @Column(name="sent_at") public LocalDateTime sentAt;
 @Column(name="issued_at",nullable=false) public LocalDateTime issuedAt;
}