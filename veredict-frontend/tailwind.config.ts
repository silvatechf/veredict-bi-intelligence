import type { Config } from "tailwindcss";

const config: Config = {
  // ✅ Adicionado o diretório shared se houver, e garantindo leitura de todas as pastas relevantes
  content: [
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/services/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/utils/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        // Inter para leitura e Mono para dados de auditoria
        sans: ["var(--font-inter)", "ui-sans-serif", "system-ui"],
        mono: ["var(--font-roboto-mono)", "ui-monospace", "SFMono-Regular"],
      },
      colors: {
        brand: {
          // Ajustado para o Emerald-500 (Padrão de sucesso executivo)
          green: '#10b981', 
          blue: '#06b6d4',
          rose: '#f43f5e', // Para o Risk Radar
        },
        // Escala ultra-dark para profundidade máxima
        zinc: {
          950: '#020202',
          900: '#09090b',
        }
      },
      animation: {
        "fade-in": "fade-in 0.8s ease-out",
        "slide-in-from-bottom": "slide-in-from-bottom 0.7s cubic-bezier(0.16, 1, 0.3, 1)",
        "slide-in-from-top": "slide-in-from-top 0.7s cubic-bezier(0.16, 1, 0.3, 1)",
        "zoom-in": "zoom-in 0.6s cubic-bezier(0.16, 1, 0.3, 1)",
        "pulse-slow": "pulse 4s cubic-bezier(0.4, 0, 0.6, 1) infinite",
        "shimmer": "shimmer 2s linear infinite",
      },
      keyframes: {
        "fade-in": {
          "0%": { opacity: "0" },
          "100%": { opacity: "1" },
        },
        "slide-in-from-bottom": {
          "0%": { transform: "translateY(30px)", opacity: "0" },
          "100%": { transform: "translateY(0)", opacity: "1" },
        },
        "slide-in-from-top": {
          "0%": { transform: "translateY(-30px)", opacity: "0" },
          "100%": { transform: "translateY(0)", opacity: "1" },
        },
        "zoom-in": {
          "0%": { transform: "scale(0.98)", opacity: "0" },
          "100%": { transform: "scale(1)", opacity: "1" },
        },
        "shimmer": {
          "100%": { transform: "translateX(100%)" },
        }
      },
      backgroundImage: {
        // Gradiente para o efeito de vidro
        'glass-gradient': 'linear-gradient(135deg, rgba(255, 255, 255, 0.03) 0%, rgba(255, 255, 255, 0.01) 100%)',
      }
    },
  },
  plugins: [
    require("tailwindcss-animate"), // Mantido: Essencial para o dashboard
  ],
};

export default config;