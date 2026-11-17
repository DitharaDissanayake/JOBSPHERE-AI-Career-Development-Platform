package com.jobsphere.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="media_recommendations")
public class MediaRecommendation {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(name="user_id",nullable=false) public Long userId;
 @Column(nullable=false,length=20) public String mediaType;
 @Column(nullable=false,length=255) public String title;
 @Column(nullable=false,length=500) public String url;
 @Column(length=500) public String embedUrl;
 @Column(columnDefinition="TEXT") public String description;
 @Column(length=160) public String source;
 @Column(length=120) public String category;
 @Column(length=160) public String position;
 @Column(name="created_at",nullable=false) public LocalDateTime createdAt;
}
