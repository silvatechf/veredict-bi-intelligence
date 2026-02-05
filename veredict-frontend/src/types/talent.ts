/**
 * 🏛️ SOVEREIGN TALENT ENTITY
 * A união da Identidade do Candidato com a Inteligência Neural do Match.
 */
export interface Talent {
  id: number;
  name: string;
  email: string;
  candidateName?: string; // Fallback para compatibilidade com o Java
  candidateEmail?: string;
  
  // 🎯 SCORES DE ELITE (0-100)
  matchScore: number;
  technicalFit: number;
  contextFit: number;

  // 🛡️ MATRIX DE RISCO NEURAL (0-100 para o Radar)
  technicalGapRisk: number;
  seniorityMismatchRisk: number;
  contextDriftRisk: number;
  scalabilityRisk: number;

  // 💡 INSIGHTS ESTRATÉGICOS
  aiSummary?: string; // O veredito para o Dossiê
  justification?: string;
  technicalGapInsight?: string;
  seniorityInsight?: string;
  contextDriftInsight?: string;
  scalabilityInsight?: string;

  // 📝 ROTEIRO INVESTIGATIVO
  interviewQuestions?: string[] | string;

  // 📅 METADADOS
  seniority?: string;
  professionalTarget?: string;
  createdAt: string;
  status?: 'PENDING' | 'ANALYZED' | 'RECALIBRATING';
}

/**
 * 🛡️ ARENA INTEGRITY GUARD
 */
export function isValidTalent(obj: any): obj is Talent {
  return obj && typeof obj.id === 'number' && typeof obj.matchScore === 'number';
}