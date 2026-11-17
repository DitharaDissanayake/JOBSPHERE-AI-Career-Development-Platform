package com.jobsphere.repository;

import com.jobsphere.entity.Executive;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExecutiveRepository extends JpaRepository<Executive,Long> {
    List<Executive> findByActiveTrueOrderByCompanyAscNameAsc();
}
