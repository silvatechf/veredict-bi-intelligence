// Local: backend/src/main/java/com/ajh/job/infrastructure/persistence/repository/JpaJobRepository.java
package com.ajh.job.infrastructure.persistence.repository;

import com.ajh.job.infrastructure.persistence.entity.JobOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaJobRepository extends JpaRepository<JobOrderEntity, Long> {
}