package com.jobsphere.entity;
import jakarta.persistence.*;
@Entity @Table(name="courses")
public class Course {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,unique=true,length=180) public String name;
 @Column(nullable=false,length=120) public String category;
 @Column(nullable=false) public Integer durationMinutes=30;
 @Column(length=500) public String shortDescription;
 @Column(columnDefinition="TEXT") public String introduction;
 @Column(columnDefinition="TEXT") public String eligibility;
 @Column(columnDefinition="TEXT") public String outcomes;
 @Column(columnDefinition="TEXT") public String modulesJson;
 @Column(columnDefinition="TEXT") public String industryApplication;
 @Column(columnDefinition="TEXT") public String tags;
 @Column(nullable=false) public Boolean active=true;
 @Column(name="course_level", length=20) public String courseLevel="BASIC";
 @Column(name="duration_days") public Integer durationDays=0;
 @Column(precision=10, scale=2) public java.math.BigDecimal price=java.math.BigDecimal.ZERO;
}