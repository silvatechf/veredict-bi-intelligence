'use client';

import { api } from './api';
import { Talent, VeredictStatus } from '@/types/talent';

/**
 * 🛰️ TALENT SERVICE: THE AUDITOR
 * Responsável por buscar e normalizar os ativos neurais do backend.
 */
export const talentService = {
  
  /**
   * 📡 GET RANKED MATCHES
   * Recupera a lista de candidatos auditados para uma vaga específica.
   */
  async getRankedMatches(jobId: number, userId: number): Promise<Talent[]> {
    try {
      // 🛡️ Proteção contra IDs inválidos antes da chamada
      if (!jobId || isNaN(jobId)) return [];

      const response = await api.get(`/jobs/${jobId}/ranking`, { 
        params: { userId } 
      });

      const rawData = Array.isArray(response.data) ? response.data : [];
      
      // 🔄 Mapeamento e Normalização em tempo real
      return rawData.map((item: any) => this.mapToTalent(item));
    } catch (error) {
      console.error(">>> [TALENT_SERVICE] Critical Link Failure:", error);
      return []; // Retorna lista vazia para evitar quebras no .map() do frontend
    }
  },

  /**
   * 📤 UPLOAD CANDIDATES
   * Envia PDFs/Currículos para processamento na Engine.
   */
  async uploadCandidates(formData: FormData): Promise<void> {
    await api.post('/candidates/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  /**
   * 🗑️ DELETE TALENT
   * Remove um ativo da Matrix.
   */
  async deleteTalent(id: number): Promise<void> {
    if (!id) return;
    await api.delete(`/candidates/${id}`);
  },

  /**
   * 🧠 MAP TO TALENT (A Ponte de Inteligência)
   * Traduz o JSON bruto do Java para a Tipagem Soberana do Frontend.
   */
  mapToTalent(item: any): Talent {
    // 1. Normalização de Scores (Garante escala 0-100)
    const normalize = (val: any) => {
      const num = Number(val ?? 0);
      return num <= 1.05 && num > 0 ? num * 100 : num;
    };

    const matchScore = normalize(item.matchScore ?? item.match_score);

    // 2. Determinação do Veredito Tático
    let veredict: VeredictStatus = 'NOT_RECOMMENDED';
    if (matchScore >= 80) veredict = 'APPROVED';
    else if (matchScore >= 55) veredict = 'CONDITIONAL';

    // 3. Parser de Perguntas (Lida com Array ou String com quebra de linha)
    const parseQuestions = (q: any): string[] => {
      if (Array.isArray(q)) return q;
      if (typeof q === 'string') return q.split('\n').filter(line => line.trim() !== '');
      return [];
    };

    return {
      id: item.id ?? item.candidateId ?? 0,
      name: item.name ?? item.candidateName ?? 'Unidentified Asset',
      email: item.email ?? item.candidateEmail ?? 'N/A',
      professionalTarget: item.professionalTarget ?? item.professional_target ?? 'Technical Specialist',
      role: item.role ?? 'CANDIDATE',
      
      // Scores
      matchScore: Number(matchScore.toFixed(2)),
      technicalFit: normalize(item.technicalFit ?? item.technical_fit),
      contextFit: normalize(item.contextFit ?? item.context_fit),
      
      // Riscos (Os 4 Pilares)
      technicalGapRisk: normalize(item.technicalGapRisk ?? item.technical_gap_risk),
      seniorityMismatchRisk: normalize(item.seniorityMismatchRisk ?? item.seniority_mismatch_risk),
      contextDriftRisk: normalize(item.contextDriftRisk ?? item.context_drift_risk),
      scalabilityRisk: normalize(item.scalabilityRisk ?? item.scalability_risk),

      // Insights
      aiSummary: item.aiSummary ?? item.ai_summary ?? "Neural synthesis active.",
      technicalGapInsight: item.technicalGapInsight ?? item.technical_gap_insight ?? "Audit vectors not identified.",
      seniorityInsight: item.seniorityInsight ?? item.seniority_insight ?? "Audit vectors not identified.",
      contextDriftInsight: item.contextDriftInsight ?? item.context_drift_insight ?? "Audit vectors not identified.",
      scalabilityInsight: item.scalabilityInsight ?? item.scalability_insight ?? "Audit vectors not identified.",
      
      // Metadados
      skills: Array.isArray(item.skills) ? item.skills : [],
      interviewQuestions: parseQuestions(item.interviewQuestions ?? item.interview_questions),
      veredictStatus: veredict,
      seniority: (item.seniority || 'SENIOR').toUpperCase(),
      naturalIdentity: item.naturalIdentity ?? item.natural_identity ?? `NODE-${Math.random().toString(36).substring(7).toUpperCase()}`
    };
  }
};