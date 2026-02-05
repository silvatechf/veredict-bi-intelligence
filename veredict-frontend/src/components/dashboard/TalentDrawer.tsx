'use client';

import React, { useEffect } from 'react';
import { 
  X, Brain, Fingerprint, Activity, FileText, Target, ChevronLeft, Zap, ShieldCheck
} from 'lucide-react';
import { Talent } from '@/services/talentService'; // Import unificado para evitar quebra de nós
import { exportExecutiveVerdict } from '@/utils/exportUtils';

// 🚀 INTEGRATED NEURAL TREE
import StrategicRoadmap from './StrategicRoadmap'; 
import TopologyRadar from '@/components/shared/charts/TopologyRadar';

interface TalentDrawerProps {
  candidate: Talent | null;
  isOpen: boolean;
  onClose: () => void;
  onRefresh: () => void;
}

export default function TalentDrawer({ candidate, isOpen, onClose, onRefresh }: TalentDrawerProps) {
  
  // ⌨️ PROTOCOLO ESCAPE: Fechar instantaneamente no teclado
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    if (isOpen) window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  if (!candidate) return null;

  // Configuração Semântica de Status baseada no Score Neural
  const getStatus = (score: number) => {
    if (score >= 80) return { label: 'MISSION_CRITICAL', risk: 'text-brand-green bg-brand-green/10 border-brand-green/20' };
    if (score >= 60) return { label: 'POTENTIAL_ALIGNMENT', risk: 'text-amber-500 bg-amber-500/10 border-amber-500/20' };
    return { label: 'HIGH_FRICTION_RISK', risk: 'text-rose-500 bg-rose-500/10 border-rose-500/20' };
  };

  const config = getStatus(candidate.matchScore || 0);

  return (
    <>
      {/* 🎭 CINEMATIC OVERLAY - Clique fora para fechar */}
      <div 
        className={`fixed inset-0 bg-black/80 backdrop-blur-xl z-[100] transition-opacity duration-500 ${
          isOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'
        }`} 
        onClick={onClose} 
      />

      {/* 🛡️ AUDIT PANEL - Calibrado para Duration-500 (Velocidade Executiva) */}
      <div className={`fixed right-0 top-0 h-full w-full max-w-4xl bg-[#030303] border-l border-white/5 z-[101] transform transition-all duration-500 ease-[cubic-bezier(0.2,0.8,0.2,1)] shadow-[-50px_0_150px_rgba(0,0,0,1)] ${
        isOpen ? 'translate-x-0' : 'translate-x-full'
      }`}>
        
        {/* TOP PRECISION LINE (Neural Handshake) */}
        <div className="h-1 w-full bg-zinc-900 overflow-hidden shrink-0">
           <div 
             className={`h-full transition-all duration-[2000ms] ease-out shadow-[0_0_20px_#10b981] bg-brand-green`}
             style={{ width: isOpen ? `${candidate.matchScore}%` : '0%' }}
           />
        </div>

        {/* HEADER: BI DOSSIER IDENTITY */}
        <div className="p-10 border-b border-white/[0.03] flex items-center justify-between bg-black/40 backdrop-blur-3xl sticky top-0 z-20">
          <div className="flex items-center gap-8">
            <button onClick={onClose} className="p-2 -ml-4 hover:bg-white/5 rounded-full text-zinc-500 hover:text-brand-green transition-all group">
               <ChevronLeft size={24} className="group-hover:-translate-x-1 transition-transform" />
            </button>

            <div className="w-16 h-16 rounded-2xl flex items-center justify-center border bg-white/[0.02] border-white/10 text-brand-green shadow-2xl">
               <Fingerprint size={28} strokeWidth={1.5} />
            </div>

            <div className="min-w-0">
              <div className="flex items-center gap-4 mb-2">
                <h2 className="text-2xl font-black text-white uppercase italic leading-none truncate">
                  {candidate.name || candidate.candidateName}
                </h2>
                <span className={`px-2 py-0.5 rounded-md border text-[7px] font-black tracking-widest font-mono ${config.risk}`}>
                  {config.label}
                </span>
              </div>
              <div className="flex items-center gap-4 text-zinc-500">
                 <span className="text-[10px] font-mono uppercase font-bold">{candidate.email || 'NODATA@VEREDICT.LAB'}</span>
                 <div className="w-1 h-1 rounded-full bg-zinc-800" />
                 <span className="text-brand-green/60 text-[10px] font-black uppercase tracking-widest italic">{candidate.seniority || 'SENIOR_NODE'}</span>
              </div>
            </div>
          </div>

          <button 
            onClick={onClose} 
            className="w-12 h-12 flex items-center justify-center bg-white/5 rounded-2xl hover:bg-rose-500/10 transition-all border border-white/10 group"
          >
            <X size={20} className="group-hover:rotate-90 transition-transform duration-300 text-zinc-500 group-hover:text-rose-500" />
          </button>
        </div>

        {/* 📜 AUDIT BODY */}
        <div className="p-12 overflow-y-auto h-[calc(100vh-120px)] custom-scrollbar space-y-16 pb-48">
          
          {/* 📊 1. KPIS MICRO-GRID (Neural Core Stats) */}
          <div className="grid grid-cols-3 gap-6">
            {[
              { label: 'Neural Precision', val: candidate.matchScore, color: 'text-brand-green', icon: Activity },
              { label: 'Technical Node', val: candidate.technicalFit, color: 'text-blue-400', icon: Zap },
              { label: 'Context Integrity', val: candidate.contextFit, color: 'text-purple-400', icon: ShieldCheck }
            ].map((stat, i) => (
              <div key={i} className="p-8 rounded-[2rem] bg-white/[0.01] border border-white/[0.05] group hover:border-white/10 transition-all shadow-xl">
                <div className="flex items-center gap-3 mb-3">
                  <stat.icon size={12} className="text-zinc-600" />
                  <p className="text-[8px] uppercase tracking-[0.4em] text-zinc-600 font-black italic">{stat.label}</p>
                </div>
                <div className="flex items-baseline gap-1">
                  <span className={`text-4xl font-black font-mono tracking-tighter ${stat.color}`}>
                    {Math.round(stat.val || 0)}%
                  </span>
                </div>
              </div>
            ))}
          </div>

          {/* 🧠 2. AI EXECUTIVE SYNTHESIS */}
          <section className="space-y-6">
            <div className="flex items-center gap-4">
               <Brain size={18} className="text-brand-green opacity-40" />
               <h3 className="text-[10px] font-black uppercase tracking-[0.6em] text-zinc-500 italic">Executive Summary</h3>
               <div className="h-px flex-1 bg-white/5" />
            </div>
            <div className="p-10 rounded-[3rem] bg-zinc-900/20 border border-white/[0.03] relative group backdrop-blur-md border-l-brand-green/40 border-l-2">
              <p className="text-xl text-zinc-300 leading-relaxed italic font-serif opacity-90 first-letter:text-5xl first-letter:font-black first-letter:text-brand-green first-letter:mr-2 first-letter:not-italic">
                "{candidate.aiSummary || candidate.whyNotHundred || "Analysis verified within mission parameters. Asset demonstrates high structural resonance."}"
              </p>
            </div>
          </section>

          {/* 🎯 3. NEURAL TOPOLOGY (Radar Detail) */}
          <section className="space-y-6">
            <div className="flex items-center gap-4">
               <Activity size={18} className="text-blue-500 opacity-40" />
               <h3 className="text-[10px] font-black uppercase tracking-[0.6em] text-zinc-500 italic">Collision Matrix</h3>
               <div className="h-px flex-1 bg-white/5" />
            </div>
            <div className="bg-black/60 rounded-[3rem] border border-white/5 p-12 min-h-[450px] shadow-2xl flex items-center justify-center relative overflow-hidden group">
               <div className="absolute inset-0 bg-brand-green/[0.01] opacity-0 group-hover:opacity-100 transition-opacity" />
               <TopologyRadar candidate={candidate} />
            </div>
          </section>

          {/* 🏁 4. INVESTIGATIVE ROADMAP */}
          <StrategicRoadmap 
            questions={candidate.interviewQuestions || []} 
            aiInsights={{
              technicalGapInsight: candidate.technicalGapInsight,
              seniorityInsight: candidate.seniorityInsight,
              contextDriftInsight: candidate.contextDriftInsight,
              scalabilityInsight: candidate.scalabilityInsight
            }}
          />

          {/* 📥 EXPORT COMMAND (Final Decision Path) */}
          <div className="pt-10">
             <button 
               onClick={() => exportExecutiveVerdict(candidate)}
               className="w-full py-8 bg-brand-green text-black rounded-[2.5rem] font-black text-xs uppercase tracking-[0.5em] italic hover:scale-[1.01] transition-all shadow-[0_20px_50px_-15px_rgba(16,185,129,0.3)] active:scale-[0.98] flex items-center justify-center gap-4 group"
             >
                <FileText size={20} className="group-hover:rotate-12 transition-transform" />
                Export Strategic Audit Record (€49)
             </button>
             <p className="text-center mt-6 text-[8px] font-mono text-zinc-800 uppercase tracking-widest opacity-40">
               Authorized Access Only // v17.4 Audit Protocol // Board Sovereign
             </p>
          </div>
        </div>
      </div>
    </>
  );
}