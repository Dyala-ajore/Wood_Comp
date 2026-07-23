package com.woodcompany.repository;

import com.woodcompany.entity.QuoteRequest;
import com.woodcompany.entity.QuoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteRequestRepository extends JpaRepository<QuoteRequest, Long> {
    List<QuoteRequest> findByUserId(Long userId);
    List<QuoteRequest> findByStatus(QuoteStatus status);
}