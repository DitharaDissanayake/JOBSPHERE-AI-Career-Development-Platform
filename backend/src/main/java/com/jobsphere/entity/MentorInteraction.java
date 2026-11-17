package com.jobsphere.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="mentor_interactions",
       indexes = {
           @Index(name="idx_mentor_interaction_user", columnList="user_id"),
           @Index(name="idx_mentor_interaction_executive", columnList="executive_id")
       })
public class MentorInteraction {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;

    @Column(name="user_id", nullable=false)
    public Long userId;

    @Column(name="executive_id", nullable=false)
    public Long executiveId;

    @Column(nullable=false, length=40)
    public String action;

    @Column(name="created_at", nullable=false)
    public LocalDateTime createdAt;
}
