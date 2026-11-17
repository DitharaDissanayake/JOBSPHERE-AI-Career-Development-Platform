package com.jobsphere.entity;
import jakarta.persistence.*;
@Entity @Table(name="jobs")
public class Job { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @Column(nullable=false) public String title; @Column(nullable=false) public String company; @Column(nullable=false) public String location; @Column(nullable=false,columnDefinition="TEXT") public String skills; @Column(name="employment_type") public String employmentType; @Column(columnDefinition="TEXT") public String description; }
