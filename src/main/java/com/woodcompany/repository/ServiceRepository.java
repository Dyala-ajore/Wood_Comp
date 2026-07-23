package com.woodcompany.repository;

import com.woodcompany.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    // اسم الـ interface "Service" ممكن يتعارض مع
    // org.springframework.stereotype.Service عند الاستيراد بملفات تانية،
    // خلي بالك تستورد com.woodcompany.entity.Service بشكل صريح لما تحتاجه
}