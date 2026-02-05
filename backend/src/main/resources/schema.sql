-- 1. ESTRUTURA DE CANDIDATOS (O CORAÇÃO DO DASHBOARD)
CREATE TABLE IF NOT EXISTS candidates (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(50),
    match_score DOUBLE PRECISION DEFAULT 0.0,
    technical_fit DOUBLE PRECISION DEFAULT 0.0,
    context_fit DOUBLE PRECISION DEFAULT 0.0,
    risk_factor VARCHAR(50) DEFAULT 'LOW',
    seniority VARCHAR(50),
    assessment_status VARCHAR(50) DEFAULT 'PENDING',
    ai_summary TEXT,
    why_not_hundred TEXT,
    interview_questions TEXT
);

-- 2. ESTRUTURA DE SKILLS (VINCULADA AOS CANDIDATOS)
CREATE TABLE IF NOT EXISTS candidate_tech_skills (
    candidate_id BIGINT NOT NULL,
    skill_name VARCHAR(255),
    CONSTRAINT fk_candidate FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE
);

-- 3. ESTRUTURA DE MATCHES (SEU ARQUIVO ORIGINAL)
CREATE TABLE IF NOT EXISTS matches (
    id SERIAL PRIMARY KEY,
    candidate_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    match_score DOUBLE PRECISION,
    fit_justification TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_match_candidate FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE
);

-- 4. SKILLS FALTANTES NO MATCH
CREATE TABLE IF NOT EXISTS match_missing_skills (
    match_id BIGINT NOT NULL,
    skill_name VARCHAR(255),
    CONSTRAINT fk_match FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE
);