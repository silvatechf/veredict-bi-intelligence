FROM python:3.10-slim

# 1. Configurações de ambiente
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1
ENV PYTHONPATH=/app/src

WORKDIR /app

# 2. Instala dependências do sistema
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 3. Instala dependências Python (Otimizado)
COPY requirements.txt .

RUN pip install --no-cache-dir --upgrade pip && \
    # Instalamos o Torch CPU e o Sentence Transformers separadamente para garantir o cache pesado
    pip install --no-cache-dir torch==2.3.1 --index-url https://download.pytorch.org/whl/cpu && \
    pip install --no-cache-dir --default-timeout=100 -r requirements.txt

# 4. Pré-download do Modelo de Embedding (O SEGREDO DA VELOCIDADE)
# Isso faz com que o container já suba com o modelo all-mpnet-base-v2, 
# evitando que ele tente baixar 400MB na primeira requisição e dê timeout.
RUN python -c "from sentence_transformers import SentenceTransformer; SentenceTransformer('all-mpnet-base-v2')"

# 5. Copia o código fonte (Deixamos por último para não quebrar o cache do pip)
COPY src/ ./src/

# 6. Define o comando de inicialização apontando para a pasta src
# Usamos 'src.main:app' porque agora o PYTHONPATH aponta para a raiz correta
CMD ["uvicorn", "src.main:app", "--host", "0.0.0.0", "--port", "8000", "--log-level", "info"]