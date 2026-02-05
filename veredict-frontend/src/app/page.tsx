'use client';

import React, { useState, useEffect } from 'react';
import { 
  ShieldCheck, Zap, ArrowRight, Loader2, BarChart3, Fingerprint, Globe, Target, Terminal
} from 'lucide-react';
import { useRouter } from 'next/navigation';
import { toast } from 'sonner';

export default function LandingPage() {
  const router = useRouter();
  const [email, setEmail] = useState('');
  const [isSyncing, setIsSyncing] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);

  const handleRequestAccess = (e: React.FormEvent) => {
    e.preventDefault();
    if (!email) return;
    setIsSyncing(true);
    
    setTimeout(() => {
      setIsSyncing(false);
      setIsSuccess(true);
      toast.success("Neural Identity Verified. Cluster Access Granted.", {
        icon: <ShieldCheck size={16} className="text-brand-green" />
      });
    }, 2800);
  };

  return (
    <div className="flex flex-col min-h-screen relative overflow-hidden bg-[#000000] text-white">
      
      {/* 🌌 ATMOSPHERIC LAYER: THE NEURAL MESH */}
      <div className="absolute inset-0 z-0 pointer-events-none">
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_50%_50%,_rgba(16,185,129,0.12)_0%,_transparent_70%)]" />
        <div className="absolute inset-0 bg-[linear-gradient(to_right,#80808008_1px,transparent_1px),linear-gradient(to_bottom,#80808008_1px,transparent_1px)] bg-[size:60px_60px]" />
        
        {/* Radar Rings */}
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[800px] h-[800px] border border-brand-green/5 rounded-full animate-pulse" />
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[1200px] h-[1200px] border border-brand-green/5 rounded-full opacity-20" />
      </div>

      {/* 🛡️ NAV BAR */}
      <nav className="p-10 flex justify-between items-center max-w-[1600px] mx-auto w-full relative z-50">
        <div className="flex items-center gap-4">
          <div className="w-10 h-10 bg-black border border-brand-green/40 rounded-xl flex items-center justify-center shadow-[0_0_20px_rgba(16,185,129,0.2)]">
            <ShieldCheck size={20} className="text-brand-green" />
          </div>
          <div className="flex flex-col">
            <span className="text-2xl font-black tracking-tighter uppercase italic leading-none">
              Veredict<span className="text-brand-green ml-0.5">BI</span>
            </span>
            <span className="text-[7px] font-mono tracking-[0.5em] text-zinc-600 mt-1 uppercase">Sovereign_Intelligence_Node</span>
          </div>
        </div>
        <button 
          onClick={() => router.push('/dashboard')}
          className="text-[10px] font-black uppercase tracking-[0.4em] text-zinc-500 hover:text-brand-green transition-all"
        >
          Access_Portal
        </button>
      </nav>

      {/* 🎯 HERO: THE NEURAL COMMAND */}
      <main className="flex-1 flex flex-col items-center justify-center px-6 text-center relative z-30 pt-10 pb-20">
        
        <div className="inline-flex items-center gap-3 bg-white/[0.03] border border-white/10 px-6 py-2 rounded-full mb-12 backdrop-blur-xl">
          <div className="w-1.5 h-1.5 rounded-full bg-brand-green animate-pulse" />
          <span className="text-[9px] font-black uppercase tracking-[0.4em] text-zinc-400">Tactical_Audit_Core // Global_Link_v1.7</span>
        </div>

        <h1 className="text-6xl md:text-[130px] font-serif italic text-white leading-[0.85] tracking-tighter mb-12 animate-in fade-in zoom-in-95 duration-1000">
          Precision <br /> 
          <span className="font-black not-italic uppercase text-transparent bg-clip-text bg-gradient-to-b from-white to-zinc-600">Without Bias.</span>
        </h1>

        <p className="max-w-2xl mx-auto text-zinc-500 text-lg md:text-xl leading-relaxed mb-20 font-medium tracking-tight">
          Quantify talent risk through <span className="text-white italic">Neural Topology Mapping</span>. 
          VeredictBI provides the sovereign decision axis for high-stakes strategic assets.
        </p>

        {/* ACCESS TERMINAL */}
        <div className="max-w-2xl mx-auto w-full relative">
          {!isSuccess ? (
            <form onSubmit={handleRequestAccess} className="relative">
              <div className="relative flex items-center p-3 bg-black border border-white/10 rounded-[2.5rem] focus-within:border-brand-green/40 transition-all shadow-[0_50px_100px_-20px_rgba(0,0,0,1)]">
                <input 
                  type="email" required disabled={isSyncing} value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="ID_CREDENTIALS@CORPORATE.NODE"
                  className="bg-transparent border-none focus:ring-0 text-sm w-full px-8 text-white placeholder:text-zinc-900 font-bold tracking-[0.1em] font-mono"
                />
                <button 
                  disabled={isSyncing}
                  className="bg-brand-green text-black h-16 px-12 rounded-[1.8rem] font-black text-[11px] uppercase tracking-widest hover:brightness-110 transition-all flex items-center gap-3 active:scale-95 disabled:opacity-50"
                >
                  {isSyncing ? <Loader2 size={16} className="animate-spin" /> : <Zap size={16} fill="currentColor" />}
                  {isSyncing ? "Verifying..." : "Initialize"}
                </button>
              </div>
              {isSyncing && (
                <div className="absolute -bottom-12 left-10 right-10 h-[1px] bg-zinc-900 rounded-full overflow-hidden">
                  <div className="h-full bg-brand-green shadow-[0_0_15px_#10b981] animate-scan" />
                </div>
              )}
            </form>
          ) : (
            <div className="p-16 glass-card rounded-[4rem] animate-in zoom-in-95 duration-700 shadow-[0_0_100px_rgba(16,185,129,0.1)] border-brand-green/20 bg-zinc-950/50 backdrop-blur-xl">
               <Fingerprint size={60} className="text-brand-green mx-auto mb-8 animate-pulse" />
               <h3 className="text-4xl font-black text-white uppercase italic tracking-tighter mb-4">Node_Authenticated</h3>
               <button onClick={() => router.push('/dashboard')} className="w-full bg-white text-black h-20 rounded-3xl font-black text-xs uppercase tracking-[0.5em] italic hover:scale-[1.02] transition-all">
                 [ Start Command Session ]
               </button>
            </div>
          )}
        </div>
      </main>

      {/* 📊 FEATURES MATRIX */}
      <section className="max-w-[1400px] mx-auto px-6 pb-48 grid grid-cols-1 md:grid-cols-3 gap-12 relative z-40">
        {[
          { 
            icon: <Target size={28} className="text-brand-green" />, 
            title: "Topology Audit", 
            desc: "Extract candidate DNA. We map technical archetypes into a multidimensional grid, revealing the architectural truth behind every asset." 
          },
          { 
            icon: <ShieldCheck size={28} className="text-brand-green" />, 
            title: "Decision Sovereign", 
            desc: "Autonomous audit nodes that quantify seniority risk and scalability potential, providing board-ready strategic intelligence." 
          },
          { 
            icon: <BarChart3 size={28} className="text-brand-green" />, 
            title: "EDD Reporting", 
            desc: "Proprietary Executive Decision Dossiers. A unified data record that mitigates the €150k+ cost of a strategic technical misfire." 
          }
        ].map((f, i) => (
          <div key={i} className="group p-12 bg-white/[0.02] border border-white/5 rounded-[4rem] transition-all duration-1000 hover:border-brand-green/30 hover:bg-brand-green/[0.02] relative overflow-hidden flex flex-col items-start shadow-2xl">
            <div className="mb-12 p-6 bg-black rounded-3xl border border-white/10 group-hover:border-brand-green/40 transition-all duration-700">
               {f.icon}
            </div>
            <h3 className="text-3xl font-black text-white mb-6 italic uppercase tracking-tighter leading-none group-hover:text-brand-green transition-colors">
              {f.title}
            </h3>
            <p className="text-base text-zinc-500 leading-relaxed font-medium group-hover:text-zinc-400 transition-colors">
              {f.desc}
            </p>
            <div className="mt-12 flex items-center gap-3">
              <div className="w-8 h-[1px] bg-brand-green/30 group-hover:w-16 transition-all duration-700" />
              <span className="text-[8px] font-mono font-black text-brand-green/60 uppercase tracking-[0.4em]">Node_Link_Ready</span>
            </div>
          </div>
        ))}
      </section>

      {/* 🏁 SYSTEM FOOTER */}
      <footer className="py-20 text-center border-t border-white/5 relative z-50 bg-[#000000]">
        <div className="flex flex-col items-center gap-4">
          <p className="text-[10px] text-zinc-800 uppercase tracking-[1em] font-black italic">
            VeredictBI // Global Tactical Network // Decision Sovereign
          </p>
          <div className="flex gap-8 text-[8px] font-mono text-zinc-900 uppercase tracking-widest">
            <span>Auth_UID: 0x77-ARENA</span>
            <span>Cluster_Status: 100%_SECURE</span>
            <span>Ref_Date: 2026_VERSION</span>
          </div>
        </div>
      </footer>
    </div>
  );
}