// Local: backend/src/main/java/com/ajh/job/application/port/out/JobRepositoryPort.java
package com.ajh.job.application.port.out;

import com.ajh.job.domain.model.JobOrder;
import java.util.List;
import java.util.Optional;

public interface JobRepositoryPort {
    JobOrder save(JobOrder jobOrder);
    Optional<JobOrder> findById(Long id);
    List<JobOrder> findAll();
    void delete(Long id);
}