package com.ajh.job.application.service;

import com.ajh.job.infrastructure.persistence.entity.JobOrderEntity;
import com.ajh.job.infrastructure.persistence.repository.JpaJobOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobOrderService {

    private final JpaJobOrderRepository jobRepository;

    @Transactional
    public JobOrderEntity createJobOrder(JobOrderEntity jobOrder) {
        // Futuro: Aqui podemos adicionar validações de IA para a descrição da vaga
        return jobRepository.save(jobOrder);
    }

    public List<JobOrderEntity> getAllOpenJobs() {
        return jobRepository.findAll();
    }

    public JobOrderEntity getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }
}