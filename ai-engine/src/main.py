import sys
import os
import logging
import json
import re
from fastapi import FastAPI, UploadFile, File, HTTPException, Body
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI(title="Veredict BI - Neural Engine")

# --- 1. PATH ADJUSTMENT ---
current_dir = os.path.dirname(os.path.abspath(__file__)) 
if current_dir not in sys.path:
    sys.path.insert(0, current_dir)

# --- 2. DIRECT IMPORTS ---
from services.cv_parser import parse_document_content
from services.ai_service import analyze_resume_with_ai, recalculate_match_with_ai
from services.embedding_service import generate_embeddings 

# --- 3. CONFIGURATION & LOGGING ---
logging.basicConfig(
    level=logging.INFO, 
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger("Veredict-AI-Engine")

app = FastAPI(
    title="Veredict BI AI Engine", 
    version="3.5.3",
    description="Sovereign Decision Intelligence Layer"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- 📊 UTILITIES: NEURAL NORMALIZATION ---

def safe_float(value, default=0.0):
    """Filtro de precisão anti-hallucination."""
    if value is None: return default
    try:
        if isinstance(value, (int, float)): return float(value)
        clean_val = str(value).replace('%', '').replace(',', '.').strip()
        match = re.search(r"[-+]?\d*\.\d+|\d+", clean_val)
        return float(match.group()) if match else default
    except: 
        return default

def clean_score(value):
    """Normaliza escalas da IA (0.85 -> 85.0) para o Dashboard."""
    num = safe_float(value)
    if 0.0 < num <= 1.0: 
        num = num * 100
    return round(num, 2) 

# --- 4. ENDPOINT: INITIAL NEURAL INGESTION ---
@app.post("/analyze-resume/analyze")
async def analyze_resume(file: UploadFile = File(...)):
    logger.info(f">>> [BI-ENGINE] Ingesting neural asset: {file.filename}")
    try:
        content = await file.read()
        text = parse_document_content(content, file.filename)
        
        if not text or len(text.strip()) < 50:
            raise HTTPException(status_code=400, detail="Text extraction failure.")

        analysis = analyze_resume_with_ai(text)
        
        # 🛡️ [RATE LIMIT GUARD] Verifica se o que voltou foi um erro de cota do Groq
        summary_text = str(analysis.get("aiSummary", ""))
        if "rate_limit_exceeded" in summary_text.lower() or "429" in summary_text:
            return {
                "name": "Limit Reached",
                "assessmentStatus": "INVALID_AI_RESPONSE",
                "aiSummary": "Groq API Rate Limit Exceeded. Please wait 15 minutes."
            }

        if "HARDWARE_FAILURE" in summary_text.upper() or analysis.get("name") is None:
            return {
                "name": file.filename,
                "assessmentStatus": "INVALID_AI_RESPONSE",
                "aiSummary": "Critical Failure: AI Engine returned semantic noise."
            }

        vector = generate_embeddings(text)
        
        return {
            "name": analysis.get("name"),
            "matchScore": clean_score(analysis.get("matchScore")),
            "technicalFit": clean_score(analysis.get("technicalFit")),
            "contextFit": clean_score(analysis.get("contextFit")),
            "riskFactor": safe_float(analysis.get("riskFactor")),
            "technicalGapRisk": safe_float(analysis.get("technicalGapRisk")),
            "seniorityMismatchRisk": safe_float(analysis.get("seniorityMismatchRisk")),
            "contextDriftRisk": safe_float(analysis.get("contextDriftRisk")),
            "scalabilityRisk": safe_float(analysis.get("scalabilityRisk")),
            "technicalGapInsight": analysis.get("technicalGapInsight"),
            "seniorityInsight": analysis.get("seniorityInsight"),
            "contextDriftInsight": analysis.get("contextDriftInsight"),
            "scalabilityInsight": analysis.get("scalabilityInsight"),
            "whyNotHundred": analysis.get("whyNotHundred"),
            "role": analysis.get("role"),
            "seniority": str(analysis.get("seniority") or "MID_LEVEL").upper(),
            "skills": analysis.get("skills") if isinstance(analysis.get("skills"), list) else [],
            "aiSummary": analysis.get("aiSummary"),
            "interviewQuestions": analysis.get("interviewQuestions") or [],
            "embedding": vector 
        }

    except Exception as e:
        logger.error(f"XXX [CRITICAL ENGINE ERROR]: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Neural Engine Mismatch: {str(e)}")

# --- 5. ENDPOINT: NEURAL RECALIBRATION ---
@app.post("/analyze-resume/recalculate")
async def recalculate(payload: dict = Body(...)):
    summary = payload.get("summary", "")
    jd = payload.get("jd", "")
    
    logger.info(">>> [BI-ENGINE] Neural Recalibration Initiated")
    
    try:
        verdict = recalculate_match_with_ai(summary, jd)
        
        # 🛡️ [RATE LIMIT GUARD] Se o recálculo falhar por cota, avisamos o Java
        summary_verdict = str(verdict.get("aiSummary", ""))
        if "rate_limit" in summary_verdict.lower() or "429" in summary_verdict:
             return {
                "matchScore": 0.1, # Mantém baixo para sinalizar erro visual
                "technicalFit": 0.1,
                "contextFit": 0.1,
                "whyNotHundred": "API Rate Limit: Recalibration suspended.",
                "justification": "Wait 15m for Groq token reset."
            }

        return {
            "matchScore": clean_score(verdict.get("matchScore")),
            "technicalFit": clean_score(verdict.get("technicalFit")),
            "contextFit": clean_score(verdict.get("contextFit")),
            "whyNotHundred": verdict.get("whyNotHundred", "Mission alignment is optimal."),
            "justification": verdict.get("justification", "Contextual match recalculated.")
        }
    except Exception as e:
        logger.error(f"XXX [RECALCULATE ERROR]: {str(e)}")
        return {
            "matchScore": 0.1,
            "technicalFit": 0.1,
            "contextFit": 0.1,
            "whyNotHundred": "Neural link error during recalibration."
        }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)