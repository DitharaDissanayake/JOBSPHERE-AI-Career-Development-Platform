package com.jobsphere.entity;

import jakarta.persistence.*;

@Entity @Table(name="interview_questions")
public class InterviewQuestion {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,length=120) public String category;
 @Column(nullable=false,length=160) public String position;
 @Column(nullable=false,columnDefinition="TEXT") public String question;
 @Column(name="ideal_answer",nullable=false,columnDefinition="TEXT") public String idealAnswer;
 @Column(columnDefinition="TEXT") public String explanation;
 @Column(columnDefinition="TEXT") public String tips;
 @Column(nullable=false) public Boolean active=true;
}
