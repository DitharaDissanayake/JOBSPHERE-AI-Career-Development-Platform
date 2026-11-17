package com.jobsphere.repository;
import com.jobsphere.entity.PasswordResetToken; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long>{ Optional<PasswordResetToken> findTopByUserIdAndUsedFalseOrderByCreatedAtDesc(Long userId); }
