package com.ajh.job.infrastructure.persistence.repository;

import com.ajh.job.domain.JobStatus; // <--- ADICIONE ESTA LINHA
import com.ajh.job.infrastructure.persistence.entity.JobOrderEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaJobOrderRepository extends JpaRepository<JobOrderEntity, Long> {
    List<JobOrderEntity> findByStatus(JobStatus status);
    List<JobOrderEntity> findByCompany(String company);
}