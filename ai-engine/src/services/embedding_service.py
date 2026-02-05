import os
import logging
import numpy as np
import threading
from sentence_transformers import SentenceTransformer

logger = logging.getLogger("Veredict-Embedding-Service")

# Lock para garantir que apenas uma thread carregue o modelo
_model_lock = threading.Lock()
_model = None

def get_model():
    """Garante carregamento sob demanda com thread-safety."""
    global _model
    if _model is None:
        with _model_lock:
            if _model is None: # Double-check pattern
                try:
                    os.environ['TOKENIZERS_PARALLELISM'] = 'false'
                    logger.info(">>> [BI-EMBEDDING] Powering up Neural Transformer (768d)...")
                    # Modelo otimizado para similaridade semântica
                    _model = SentenceTransformer('all-mpnet-base-v2')
                except Exception as e:
                    logger.error(f"XXX [EMBEDDING] Critical failure loading model: {e}")
                    return None
    return _model

def generate_embeddings(text: str):
    """
    Converte o texto do CV em um vetor normalizado de 768 dimensões.
    v3.4.0: Inclui L2 Normalization nativa e tratamento de densidade.
    """
    try:
        if not text or len(text.strip()) < 10:
            return [0.0] * 768

        model = get_model()
        if not model:
            return [0.0] * 768

        # --- PRÉ-PROCESSAMENTO BI ---
        # Limpeza de espaços extras mantendo a densidade semântica
        clean_text = " ".join(text.split())
        
        # 💡 Dica: Currículos longos? O modelo aceita 512 tokens. 
        # Em vez de slice de string, o próprio model.encode lida com o truncamento interno.
        # Mas limitamos a entrada para evitar sobrecarga de memória (OOM)
        safe_text = clean_text[:8000] 

        # Gera o embedding
        # normalize_embeddings=True já faz a normalização L2 via biblioteca (C++ otimizado)
        embedding = model.encode(
            safe_text, 
            convert_to_numpy=True, 
            show_progress_bar=False,
            normalize_embeddings=True 
        )

        return embedding.tolist()

    except Exception as e:
        logger.error(f"XXX [EMBEDDING ERROR]: {str(e)}")
        return [0.0] * 768