import pypdf
import re
import logging
from io import BytesIO

logger = logging.getLogger("Veredict-CV-Parser")

def parse_document_content(content: bytes, filename: str) -> str:
    """
    v3.3.1 - Estratégia de Extração de Alta Fidelidade.
    Realiza o parsing de documentos (PDF/TXT) com normalização para motores de IA.
    Focado em preservar a estrutura semântica e eliminar ruído de caracteres.
    """
    filename = filename.lower()
    raw_text = ""
    
    try:
        logger.info(f">>> [BI-PARSER] Ingesting neural asset: {filename}")
        
        # --- 1. LÓGICA DE EXTRAÇÃO POR FORMATO ---
        if filename.endswith(".pdf"):
            try:
                pdf_stream = BytesIO(content)
                reader = pypdf.PdfReader(pdf_stream)
                
                pages_text = []
                for page in reader.pages:
                    # Extração de texto pura preservando espaços
                    text = page.extract_text()
                    if text:
                        pages_text.append(text)
                
                raw_text = "\n".join(pages_text)
            except Exception as pdf_err:
                logger.error(f"XXX [PDF ERROR] Structure corrupted in {filename}: {str(pdf_err)}")
                return "Error: PDF structure corrupted or password protected."
                
        elif filename.endswith(".txt"):
            try:
                raw_text = content.decode('utf-8', errors='ignore')
            except Exception as txt_err:
                logger.error(f"XXX [TXT ERROR] Encoding failure: {str(txt_err)}")
                return "Error: TXT encoding not supported."
        else:
            logger.error(f"XXX [PARSER] Unsupported format attempt: {filename}")
            return "Error: Unsupported format. Please use PDF or TXT."

        # --- 2. NORMALIZAÇÃO PARA IA (Otimização de Contexto) ---
        
        # A. Limpeza de Caracteres de Controle
        # Mantém apenas caracteres imprimíveis e quebras de linha. 
        # Isso evita o erro de JSON Decode que vimos no terminal Python.
        text = "".join(char for char in raw_text if char.isprintable() or char in "\n")
        
        # B. Normalização Horizontal
        # Transforma tabs e múltiplos espaços em um espaço simples (preserva a linha)
        text = re.sub(r'[ \t]+', ' ', text)
        
        # C. Normalização Vertical
        # Colapsa 3 ou mais quebras de linha em apenas duas (evita "buracos" de tokens)
        text = re.sub(r'\n\s*\n+', '\n\n', text)
        
        cleaned_text = text.strip()

        # --- 3. AUDITORIA DE DENSIDADE ---
        # Currículos com menos de 50 caracteres costumam ser apenas uma imagem (sem OCR)
        if len(cleaned_text) < 50:
            logger.warning(f"!!! [PARSER] Low text density detected in {filename}. Possible image-only PDF.")
            return "Error: Document appears to be an image. Please provide a text-based PDF."

        logger.info(f">>> [PARSER SUCCESS] {len(cleaned_text)} characters extracted and sanitized.")
        return cleaned_text

    except Exception as e:
        logger.error(f"XXX [PARSER CRITICAL FAILURE] {str(e)}")
        return f"Error: Internal ingestion failure during parsing."