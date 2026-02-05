import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  reactCompiler: true,

  // 🚀 FORMATO PARA NEXT.JS 15+
  devIndicators: {
    appIsrStatus: false, // Remove o indicador de status ISR/Static
    buildActivity: false, // Garante que o indicador de build suma
  },
};

export default nextConfig;