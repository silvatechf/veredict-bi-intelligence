import type { Metadata, Viewport } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { Toaster } from 'sonner';

const inter = Inter({ 
  subsets: ["latin"],
  variable: "--font-inter",
  display: 'swap',
  weight: ['400', '700', '900'], 
});

// ✅ UNIFICADO: Apenas uma declaração de Metadata
export const metadata: Metadata = {
  title: "Veredict BI | Neural Intelligence",
  description: "Elite AI recruitment engine for sovereign decision making.",
  icons: {
    icon: '/favicon.ico',
  }
};

export const viewport: Viewport = {
  themeColor: '#020202',
  width: 'device-width',
  initialScale: 1,
  maximumScale: 1,
  userScalable: false, // Trava total para experiência de App/BI
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html 
      lang="pt-BR" 
      className={`${inter.variable} dark`} 
      style={{ colorScheme: 'dark' }}
      suppressHydrationWarning 
    > 
      <body
        className="font-sans antialiased bg-[#020202] text-white selection:bg-brand-green/30 min-h-screen relative overflow-x-hidden"
      >
        {/* 🌌 ATMOSPHERE ENGINE: Orbes pulsantes do globals.css */}
        <div className="living-bg" aria-hidden="true">
          <div className="orb orb-green" />
          <div className="orb orb-blue" />
        </div> 
        
        {/* 🔔 NOTIFICATION SYSTEM (Glassmorphism Style) */}
        <Toaster 
          position="top-right" 
          richColors 
          theme="dark" 
          closeButton
          toastOptions={{
            style: { 
              background: 'rgba(9, 9, 11, 0.9)',
              backdropFilter: 'blur(16px)',
              border: '1px solid rgba(255, 255, 255, 0.08)',
              borderRadius: '1.25rem',
              fontSize: '12px',
              fontWeight: '600',
              letterSpacing: '-0.02em'
            }
          }}
        />
        
        {/* MAIN CONTENT AREA: Garantindo o fluxo vertical */}
        <main className="relative z-10 flex flex-col min-h-screen">
          {children}
        </main>
      </body>
    </html>
  );
}