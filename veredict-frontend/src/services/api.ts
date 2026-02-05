import axios from 'axios';

/**
 * 🛰️ AXIOS INSTANCE: THE NEURAL LINK
 * Calibrado para suportar processamento pesado de IA (60s timeout).
 */
export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1',
  timeout: 60000,
  withCredentials: true, // Vital para sessões seguras com o Java Spring
});

// --- 🛡️ REQUEST INTERCEPTOR: SECURITY & HYGIENE ---
api.interceptors.request.use((config) => {
  const token = typeof window !== 'undefined' ? localStorage.getItem('token') : null;
  
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  // 🧪 CACHE BUSTER: Garante que cada colisão no radar seja inédita
  if (config.method?.toLowerCase() === 'get') {
    config.params = { ...config.params, _t: Date.now() };
  }

  // 🚨 CONTENT-TYPE HANDLER:
  // Deixa o browser gerenciar o Boundary do FormData para uploads de PDF
  if (config.data instanceof FormData) {
    if (config.headers) delete config.headers['Content-Type'];
  } else {
    config.headers['Content-Type'] = 'application/json';
  }

  return config;
}, (error) => Promise.reject(error));

// --- 🕵️ RESPONSE INTERCEPTOR: TELEMETRY & AUTO-RECOVERY ---
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const url = error.config?.url;

    // 🔴 GATEWAY & AUTH ERRORS
    if (status) {
      console.error(
        `%c 🚨 [GATEWAY ${status}] %c Failure at: ${url}`, 
        "color: white; background: #f43f5e; font-weight: 800; border-radius: 4px; padding: 2px 6px;", 
        "color: #f43f5e;"
      );

      if (status === 401 && typeof window !== 'undefined') {
        localStorage.removeItem('token');
        // Redirecionamento comentado para não interromper o desenvolvimento
        // window.location.href = '/login';
      }
    } 
    // ⏳ AI ENGINE TIMEOUT
    else if (error.code === 'ECONNABORTED') {
      console.error(
        "%c ⏳ [NEURAL TIMEOUT] %c AI Engine exceeded 60s limit. Check Python/Groq nodes.",
        "color: white; background: #fbbf24; font-weight: 800; border-radius: 4px; padding: 2px 6px;",
        "color: #fbbf24;"
      );
    }
    // 📡 CONNECTION FAILURE
    else {
      console.error(
        "%c 📡 [NETWORK OFFLINE] %c Connection to Java Backend failed.",
        "color: white; background: #3f3f46; font-weight: 800; border-radius: 4px; padding: 2px 6px;",
        "color: #71717a;"
      );
    }
    
    return Promise.reject(error);
  }
);