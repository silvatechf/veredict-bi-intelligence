'use client';

import React, { useMemo } from 'react';
import { Talent } from '@/services/talentService';
import { ShieldCheck, Activity, Fingerprint, FileText, BarChart3, Code2, AlertTriangle, CheckCircle2, XCircle, Info, Sparkles } from 'lucide-react';
import { exportExecutiveVerdict } from '@/utils/exportUtils'; 

interface ExecutiveDossierProps {
  talents: Talent[];
}

export default function ExecutiveDossier({ talents }: ExecutiveDossierProps) {
  const talent = useMemo(() => {
    if (!talents || talents.length === 0) return null;
    return talents.length === 1 
      ? talents[0] 
      : [...talents].sort((a, b) => (b.matchScore || 0) - (a.matchScore || 0))[0];
  }, [talents]);

  // 🧠 DYNAMIC IMPACT ENGINE (Refinado para narrativa de Consultoria)
  const getDynamicImpact = (t: Talent) => {
    if (!t) return "";
    
    // Prioridade 1: Falhas críticas detectadas pelo motor
    if (t.technicalGapRisk && t.technicalGapRisk > 50) return t.technicalGapInsight || "Significant technical friction detected. Asset requires intensive structural vetting.";
    if (t.seniorityMismatchRisk && t.seniorityMismatchRisk > 40) return t.seniorityInsight || "Experience delta confirmed. Potential misalignment with mission seniority nodes.";

    // Prioridade 2: Onboarding Overhead (Custo de Tempo)
    if ((t.technicalGapRisk || 0) > 25) {
      const weeks = ((t.technicalGapRisk || 0) / 10).toFixed(1);
      return `Neural markers predict an onboarding overhead of ${weeks} weeks to reach full operational liquidity.`;
    }

    // Prioridade 3: Caso de Sucesso
    if ((t.matchScore || 0) >= 80) {
      return "High-resonance asset. Model predicts immediate contribution with minimal architectural oversight.";
    }

    return t.aiSummary || "Asset demonstrates baseline stability. Standard integration protocols advised.";
  };

  const getVerdict = (score: number) => {
    if (score >= 80) return { label: 'GO / RECOMMENDED', color: 'text-brand-green', bg: 'bg-brand-green/5', border: 'border-brand-green/20', icon: CheckCircle2 };
    if (score >= 60) return { label: 'CONDITIONAL APPROVAL', color: 'text-amber-500', bg: 'bg-amber-500/5', border: 'border-amber-500/20', icon: AlertTriangle };
    return { label: 'NO-GO / HIGH RISK', color: 'text-rose-500', bg: 'bg-rose-500/5', border: 'border-rose-500/20', icon: XCircle };
  };

  if (!talent) return null;
  const verdict = getVerdict(talent.matchScore || 0);

  return (
    <div className="h-full flex flex-col glass-card p-10 md:p-12 relative overflow-hidden group shadow-[0_50px_100px_-20px_rgba(0,0,0,1)] rounded-[4rem] border-white/[0.02]">
      <div className="absolute -top-32 -right-32 w-96 h-96 bg-brand-green/[0.02] blur-[120px] rounded-full" />
      
      <div className="flex flex-col gap-8 relative z-10 h-full">
        
        {/* 🔒 LAYER 0: AUDIT METADATA */}
        <div className="flex items-center justify-between border-b border-white/[0.03] pb-6">
          <div className="flex flex-col gap-1">
            <div className="flex items-center gap-2">
              <Sparkles size={10} className="text-brand-green animate-pulse" />
              <span className="text-[10px] font-black text-white uppercase tracking-[0.2em]">Founder Access Active</span>
            </div>
            <span className="text-[9px] font-mono text-zinc-600 uppercase italic">Rate Locked: €49 // Value: €149</span>
          </div>
          <div className="text-right flex flex-col gap-1">
            <span className="text-[10px] font-black text-brand-green uppercase tracking-widest font-mono">
              NODE — {talent.naturalIdentity?.substring(0, 8).toUpperCase() || 'REF_STABLE'}
            </span>
            <span className="text-[8px] font-mono text-zinc-600 uppercase tracking-tighter">
              {new Date().toISOString().substring(0,10)} // AUDIT_COMPLETE
            </span>
          </div>
        </div>

        {/* 🟣 LAYER 1: THE SOVEREIGN VERDICT */}
        <div className={`p-8 rounded-[3rem] ${verdict.bg} border ${verdict.border} relative overflow-hidden`}>
          <div className="absolute top-0 right-0 p-6 opacity-10">
            <verdict.icon size={60} className={verdict.color} />
          </div>
          
          <h2 className={`text-4xl font-[1000] italic tracking-tighter uppercase leading-none mb-6 ${verdict.color}`}>
            {verdict.label}
          </h2>

          <div className="space-y-4">
            <div className="flex gap-3 items-start">
              <div className={`mt-1.5 w-1.5 h-1.5 rounded-full ${verdict.color} bg-current shadow-[0_0_10px_currentColor]`} />
              <p className="text-zinc-200 text-xs leading-relaxed font-semibold">
                <span className="text-white/40 font-bold uppercase mr-2 text-[9px]">Primary Impact:</span>
                {getDynamicImpact(talent)}
              </p>
            </div>

            {/* 🎯 NEURAL GAP AUDIT DINÂMICO */}
            <div className="flex gap-3 items-start pt-4 border-t border-white/5 mt-4">
              <Info size={14} className="text-zinc-500 mt-0.5 shrink-0" />
              <p className="text-zinc-400 text-[10px] leading-relaxed italic font-medium">
                <span className="text-zinc-200 font-bold uppercase not-italic mr-2">Neural Gap Audit:</span>
                {/* Prioritiza a falha real identificada no loop de colisão */}
                {talent.whyNotHundred || talent.technicalGapInsight || "Asset performance within expected architectural parameters."}
              </p>
            </div>
          </div>

          <div className="mt-8 pt-4 border-t border-white/5 flex items-center gap-2 opacity-50">
             <ShieldCheck size={10} className="text-zinc-500" />
             <p className="text-[7px] font-bold uppercase tracking-[0.2em] text-zinc-500 font-mono">
               Veredict quantifies risk. <span className="text-zinc-300">Sovereign decision remains with the board.</span>
             </p>
          </div>
        </div>

        {/* 🟢 LAYER 2: ASSET IDENTITY */}
        <div className="space-y-8 flex-1">
          <div className="max-w-full">
            <div className="flex items-center gap-3 mb-2">
              <span className="text-[9px] font-black text-brand-green bg-brand-green/10 px-3 py-0.5 rounded-full uppercase tracking-widest font-mono border border-brand-green/20">Verified Asset</span>
              <span className="text-[8px] font-mono text-zinc-600 uppercase tracking-widest">Confid: 94.22%</span>
            </div>
            <h1 className="text-4xl md:text-5xl font-black text-white tracking-tighter uppercase italic leading-[0.9] mb-4 break-words">
              {talent.name || talent.candidateName}
            </h1>
            <div className="flex flex-wrap items-center gap-4">
               <div className="flex items-center gap-2 bg-white/[0.02] px-3 py-1.5 rounded-xl border border-white/5">
                  <Fingerprint size={12} className="text-brand-green/40" />
                  <p className="text-zinc-500 font-mono text-[9px] tracking-widest uppercase font-bold italic">
                    {talent.professionalTarget || "Mission Target"}
                  </p>
               </div>
               <div className="flex items-center gap-2 text-zinc-600">
                  <Code2 size={12} />
                  <span className="text-[8px] font-black uppercase tracking-[0.2em] truncate max-w-[180px]">
                    {talent.techSkills?.slice(0, 5).join(' • ') || 'SCAN COMPLETE'}
                  </span>
               </div>
            </div>
          </div>

          {/* Audit Metrics Grid */}
          <div className="space-y-4 py-6 border-y border-white/[0.03]">
            <div className="grid grid-cols-2 gap-x-10 gap-y-5">
              {[
                { label: 'Technical Fit', value: talent.technicalFit, color: 'bg-emerald-500' },
                { label: 'Context Fit', value: talent.contextFit, color: 'bg-blue-500' },
                { label: 'Seniority Alignment', value: 100 - (talent.seniorityMismatchRisk || 0), color: 'bg-amber-500' },
                { label: 'Scalability Potential', value: 100 - (talent.scalabilityRisk || 0), color: 'bg-purple-500' }
              ].map((metric, i) => (
                <div key={i} className="space-y-2">
                  <div className="flex justify-between items-center">
                    <span className="text-[8px] font-black uppercase tracking-widest text-zinc-500">{metric.label}</span>
                    <span className="text-[10px] font-mono text-zinc-300 font-bold">{metric.value?.toFixed(0)}%</span>
                  </div>
                  <div className="h-1 w-full bg-white/5 rounded-full overflow-hidden">
                    <div className={`h-full ${metric.color} transition-all duration-[1500ms] shadow-[0_0_8px_rgba(0,0,0,0.5)]`} style={{ width: `${metric.value}%` }} />
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* 💰 MONETIZATION BLOCK (Respiro aumentado) */}
          <div className="mt-auto pt-8">
              <div className="flex items-center justify-between mb-6">
                 <div className="flex items-center gap-3">
                    <Activity size={14} className="text-brand-green/40" />
                    <span className="text-[9px] font-black uppercase tracking-[0.4em] text-zinc-600 italic">Strategic Briefing</span>
                 </div>
                 <div className="flex items-center gap-4">
                    <div className="hidden sm:flex flex-col items-end">
                      <span className="text-[7px] font-black text-brand-green uppercase tracking-tighter bg-brand-green/10 px-2 py-0.5 rounded-sm">Board-Ready</span>
                      <span className="text-[9px] font-bold text-zinc-600 line-through decoration-rose-500/40 opacity-50">€149.00</span>
                    </div>
                    <button 
                      onClick={() => exportExecutiveVerdict(talent)}
                      className="flex items-center gap-3 px-6 py-3 bg-brand-green text-black rounded-2xl text-[9px] font-black hover:scale-105 transition-all uppercase tracking-widest shadow-[0_15px_30px_-10px_#10b981] group active:scale-95 font-mono"
                    >
                      <FileText size={12} className="group-hover:rotate-6 transition-transform" />
                      Export EDD (€49)
                    </button>
                 </div>
              </div>
              <p className="text-zinc-300 text-lg italic leading-relaxed font-serif pl-8 border-l-2 border-brand-green/30 py-1 opacity-90">
                "{talent.aiSummary || "Architectural assessment finalized. Asset demonstrates high resonance with defined mission parameters."}"
              </p>
          </div>
        </div>
      </div>
    </div>
  );
}