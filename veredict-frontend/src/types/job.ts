/**
 * 🏛️ JOB ORDER ENTITY
 * Define o gabarito tático contra o qual os candidatos são auditados.
 */
export interface JobOrder {
  id: number;
  title: string;
  companyName: string; // ✅ Adicionado para bater com joe1_0.company_name do log
  description: string;
  status: 'OPEN' | 'CLOSED' | 'FILLED'; 
  
  // 📊 METADADOS DE AUDITORIA (ISO 8601 Strings)
  createdAt: string; 
  updatedAt?: string;

  // 🎯 PARÂMETROS DE CALIBRAÇÃO (Essencial para o Veredict Score)
  seniority: string; // Mantido string para aceitar "SENIOR AI ARCHITECT" ou "PROFESSIONAL NODE"
  location?: string;
  department?: string;
  salaryRange?: string; 
  
  // 🧠 NEURAL REQUIREMENTS
  // No seu log, o Hibernate busca em job_requirements.requirement_name
  // Mapeamos aqui como uma lista para o componente ArenaHeader/Comparison
  requirements?: string[]; 

  // Permite armazenar vetores de embedding ou requisitos técnicos processados pela Engine Python
  metadata?: {
    requiredSkills?: string[];
    priority?: 'HIGH' | 'MEDIUM' | 'LOW';
    [key: string]: any;
  };
}

/**
 * 📥 DATA TRANSFER OBJECTS (DTOs)
 */
export type CreateJobRequest = Omit<JobOrder, 'id' | 'createdAt' | 'updatedAt'>;

export type UpdateJobStatusRequest = Pick<JobOrder, 'id' | 'status'>;

/**
 * 🛡️ TYPE GUARD
 * Garante que o objeto processado pelo talentService é uma JobOrder válida
 */
export function isJobOrder(obj: any): obj is JobOrder {
  return obj && typeof obj.id === 'number' && typeof obj.title === 'string';
}