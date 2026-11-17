package com.jobsphere.repository;

import com.jobsphere.entity.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeAnalysisRepository
        extends JpaRepository<ResumeAnalysis, Long> {

    Optional<ResumeAnalysis> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}