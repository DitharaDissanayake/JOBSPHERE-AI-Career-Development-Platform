package com.jobsphere.entity;

import jakarta.persistence.*;

@Entity
@Table(name="career_executives")
public class Executive {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;

    @Column(nullable=false, length=160)
    public String name;

    @Column(nullable=false, length=160)
    public String company;

    @Column(nullable=false, length=160)
    public String position;

    @Column(name="phone_number", nullable=false, length=30)
    public String phoneNumber;

    @Column(name="whatsapp_number", nullable=false, length=30)
    public String whatsappNumber;

    @Column(length=255)
    public String imageUrl;

    @Column(length=120)
    public String category;

    @Column(columnDefinition="TEXT")
    public String expertise;

    @Column(columnDefinition="TEXT")
    public String profileTags;

    @Column(columnDefinition="TEXT")
    public String bio;

    @Column(nullable=false)
    public Boolean active=true;
}
