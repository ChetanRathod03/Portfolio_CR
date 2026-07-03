package com.chetanrathod.portfolio.repository;

import com.chetanrathod.portfolio.entity.RecruiterFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruiterFeedbackRepository extends JpaRepository<RecruiterFeedback, Long> {
}
