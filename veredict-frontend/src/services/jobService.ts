'use client';

import { api } from './api'; 
import { AxiosResponse } from 'axios';
import { JobOrder } from '../types/job';

export const jobService = {
  
  /**
   * 📚 GET ALL JOBS
   */
  getAllJobs: async (): Promise<JobOrder[]> => {
    try {
      const response: AxiosResponse<JobOrder[]> = await api.get('/jobs');
      if (!response.data || !Array.isArray(response.data)) return [];

      return response.data
        .map(job => jobService.normalizeJob(job))
        .filter(job => !!job.description)
        .sort((a, b) => new Date(b.createdAt || '').getTime() - new Date(a.createdAt || '').getTime());

    } catch (error) {
      console.error(">>> [JOB_SERVICE] Core Link Failure:", error);
      return [];
    }
  },

  /**
   * 📥 DEPLOY JOB CONTEXT
   * Sincronizado com @PostMapping e @PutMapping do Java
   */
  createJob: async (jobData: Partial<JobOrder>): Promise<JobOrder | null> => {
    try {
      // 🧠 Normalizamos o payload para o formato que o DTO do Java (JobRequest) espera
      const payload = {
        title: jobData.title || `MISSION_ALPHA`,
        description: jobData.description,
        company: jobData.companyName || "Veredict Global", // 🛡️ Mapeado para 'company' conforme seu DTO Java
        status: jobData.status || 'OPEN',
        seniority: jobData.seniority || 'SENIOR',
        location: jobData.location || 'Remote',
        salaryRange: jobData.salaryRange || ""
      };

      // 🛡️ Se tem ID, chama o @PutMapping("/{id}") no Java
      const response: AxiosResponse<JobOrder> = jobData.id 
        ? await api.put(`/jobs/${jobData.id}`, payload)
        : await api.post('/jobs', payload);

      return response.data ? jobService.normalizeJob(response.data) : null;
    } catch (error) {
      console.error(">>> [JOB_SERVICE] Deployment Failure:", error);
      throw error; 
    }
  },

  /**
   * 🧠 NEURAL NORMALIZATION
   */
  normalizeJob(data: any): JobOrder {
    if (!data) return {} as JobOrder;

    // Parser de Requisitos resiliente para Hibernate
    let rawReqs = data.requirements || data.job_requirements || [];
    let normalizedReqs: string[] = [];

    if (Array.isArray(rawReqs)) {
      normalizedReqs = rawReqs.map(r => 
        typeof r === 'object' ? (r.requirementName || r.name || r.requirement_name) : r
      );
    } else if (typeof rawReqs === 'string') {
      normalizedReqs = rawReqs.split(',').filter(Boolean).map(s => s.trim());
    }

    return {
      ...data,
      id: data.id || 0,
      title: data.title || data.job_title || 'Strategic Context',
      // 🛡️ Bridge: Java 'company' -> Frontend 'companyName'
      companyName: data.companyName || data.company_name || data.company || 'Veredict Intel',
      description: data.description || 'Neural context awaiting briefing...',
      requirements: normalizedReqs,
      createdAt: data.createdAt || data.created_at || new Date().toISOString(),
      status: data.status || 'OPEN'
    };
  }
};