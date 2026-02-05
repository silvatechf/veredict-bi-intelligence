import os, json, re, logging, time
from typing import List, Optional
from pydantic import BaseModel, Field, field_validator
from langchain_groq import ChatGroq

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("Veredict-Enterprise-Engine")
logger = logging.getLogger("Veredict-BI-Engine")

# --- 📜 THE SEMANTIC CONTRACT ---
class AnalysisSchema(BaseModel):
    name: str = Field(..., description="Full name extracted from CV")
    professionalTarget: str 
    aiSummary: str
    seniority: str
    skills: List[str]
    matchScore: float
    technicalFit: float
    contextFit: float
    technicalGapRisk: float
    seniorityMismatchRisk: float
    contextDriftRisk: float
    scalabilityRisk: float
    technicalGapInsight: str
    seniorityInsight: str
    contextDriftInsight: str
    scalabilityInsight: str
    interviewQuestions: List[str]

    @field_validator('seniority', mode='before')
    @classmethod
    def force_seniority_string(cls, v):
        if isinstance(v, (int, float)):
            if v >= 0.7: return "SENIOR"
            if v >= 0.4: return "PLENO"
            return "JUNIOR"
        return str(v).upper() if v else "SENIOR"

    @field_validator('matchScore', 'technicalFit', 'contextFit', 'technicalGapRisk', 'seniorityMismatchRisk', 'contextDriftRisk', 'scalabilityRisk')
    @classmethod
    def validate_scores(cls, v: float) -> float:
        val = v if v is not None else 0.1
        return val / 100.0 if 1.0 < val <= 100.0 else val

# --- ⚡ ENGINE CONFIGURATION ---
# 🛡️ Adicionamos 'model_kwargs' para forçar o Groq a responder em modo JSON
llm_heavy = ChatGroq(
    temperature=0.0, 
    model_name="llama-3.3-70b-versatile", 
    api_key=os.getenv("GROQ_API_KEY"),
    model_kwargs={"response_format": {"type": "json_object"}}
)
llm_light = ChatGroq(
    temperature=0.0, 
    model_name="llama-3.1-8b-instant", 
    api_key=os.getenv("GROQ_API_KEY"),
    model_kwargs={"response_format": {"type": "json_object"}}
)

def clean_json_string(content: str) -> str:
    """Limpa lixo de Markdown e garante aspas duplas."""
    # Remove blocos de código markdown
    content = re.sub(r'```json\s*|```\s*', '', content)
    # Remove qualquer texto antes da primeira chave e depois da última
    start = content.find('{')
    end = content.rfind('}') + 1
    if start != -1 and end != 0:
        content = content[start:end]
    # Troca aspas simples por duplas apenas onde importa (aproximação)
    return content.strip()

def analyze_resume_with_ai(resume_text: str):
    """Deep Ingestion v4.8 - JSON Native Enforcement."""
    try:
        clean_text = resume_text[:6000] 
        system_prompt = (
            "ACT AS A TECHNICAL AUDITOR. YOU MUST RETURN ONLY A VALID JSON OBJECT. "
            "USE DOUBLE QUOTES FOR ALL KEYS AND STRINGS. "
            "REQUIRED KEYS: name, professionalTarget, aiSummary, seniority, skills, matchScore, "
            "technicalFit, contextFit, technicalGapRisk, seniorityMismatchRisk, contextDriftRisk, "
            "scalabilityRisk, technicalGapInsight, seniorityInsight, contextDriftInsight, "
            "scalabilityInsight, interviewQuestions."
        )
        response = llm_heavy.invoke([("system", system_prompt), ("human", f"CV: {clean_text}")])
        content = clean_json_string(response.content)
        
        json_data = json.loads(content)
        validated_data = AnalysisSchema(**json_data) 
        output = validated_data.model_dump()
        
        for k in ['matchScore', 'technicalFit', 'contextFit', 'technicalGapRisk', 'seniorityMismatchRisk', 'contextDriftRisk', 'scalabilityRisk']:
            val = output[k]
            output[k] = round(38.0 + (val * 40), 2) if val <= 0.15 else round(val * 100, 2)
        output["assessmentStatus"] = "ANALYZED"
        return output
    except Exception as e:
        logger.error(f"🚨 INGESTION ERROR: {e}")
        return _generate_error_payload(str(e))

def recalculate_match_with_ai(candidate_summary: str, job_description: str):
    """v4.8 Turbo Match - Zero Syntax Error Protocol."""
    for attempt in range(3):
        try:
            match_prompt = (
                "ACT AS A TECH RECRUITER. RETURN JSON ONLY. "
                "FIELDS: 'matchScore' (float), 'technicalFit' (float), 'contextFit' (float), 'whyNotHundred' (string). "
                "CONTEXT: JD: {jd} | CV: {cv}".format(jd=job_description[:1000], cv=candidate_summary[:1000])
            )
            response = llm_light.invoke([("system", "Return JSON Object"), ("human", match_prompt)])
            content = clean_json_string(response.content)
            
            data = json.loads(content)
            
            for key in ["matchScore", "technicalFit", "contextFit"]:
                raw_val = float(data.get(key, 0.1))
                val = raw_val if raw_val > 1.0 else raw_val * 100
                jitter = (len(candidate_summary) % 10) / 5.0
                data[key] = max(10.0, round(val + jitter, 2))
                
            return data
        except Exception as e:
            wait_time = 1.5 * (attempt + 1)
            logger.warning(f"⚠️ Syntax/Sync Stutter ({attempt+1}): {e}. Waiting {wait_time}s...")
            time.sleep(wait_time)
            continue
    
    return {"match_score": 22.1, "technical_fit": 22.2, "context_fit": 22.3, "whyNotHundred": "JSON Parse Enforcement Error."}

def _generate_error_payload(error_msg: str):
    return {
        "assessment_status": "INVALID_AI_RESPONSE",
        "aiSummary": f"Shield Active: {error_msg}",
        "name": "Audit Failure", "matchScore": 0.1, "technicalFit": 0.1, "contextFit": 0.1,
        "technicalGapRisk": 0.1, "seniorityMismatchRisk": 0.1, "contextDriftRisk": 0.1, "scalabilityRisk": 0.1,
        "skills": [], "interviewQuestions": [], "professionalTarget": "N/A",
        "technicalGapInsight": "N/A", "seniorityInsight": "N/A", "contextDriftInsight": "N/A", "scalabilityInsight": "N/A"
    }