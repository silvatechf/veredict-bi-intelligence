'use client';

import React, { useMemo } from 'react';
import { 
  Radar, RadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, ResponsiveContainer 
} from 'recharts';
import { ShieldCheck, Zap, Activity, Maximize2, Target, Hash, MessageSquareQuote } from 'lucide-react';

interface TopologyRadarProps {
  candidate: any;
  color?: string;
}

export default function TopologyRadar({ candidate, color = "#10b981" }: TopologyRadarProps) {
  
  // 🧠 COLLISION DATA: Traduz o cruzamento entre o candidato e os requisitos da vaga
  const processedData = useMemo(() => [
    { subject: 'MATCH', candidate: candidate?.matchScore || 0, target: 100 }, 
    { subject: 'TECH_FIT', candidate: candidate?.technicalFit || 0, target: 100 },
    { subject: 'CONTEXT', candidate: candidate?.contextFit || 0, target: 100 },
    { subject: 'STABILITY', candidate: Math.max(0, 100 - (candidate?.contextDriftRisk || 0)), target: 100 },
    { subject: 'RESILIENCE', candidate: Math.max(0, 100 - (candidate?.technicalGapRisk || 0)), target: 100 },
    { subject: 'CAPACITY', candidate: Math.max(0, 100 - (candidate?.scalabilityRisk || 0)), target: 100 },
  ], [candidate]);

  const RiskBadge = ({ 
    label, value, icon: Icon 
  }: { label: string, value: number, icon: any }) => {
    const val = Number(value) || 0;
    
    // 🔥 Semântica de Alerta de BI
    const isHighRisk = val >= 50;

    return (
      <div className="bg-black/40 border border-white/[0.03] p-4 rounded-2xl group/risk transition-all hover:border-white/10">
        <div className="flex items-center justify-between mb-2">
           <div className="flex items-center gap-2">
              <Icon size={12} className={isHighRisk ? 'text-rose-500' : 'text-zinc-500'} />
              <span className="text-[8px] font-black uppercase tracking-widest text-zinc-500">{label}</span>
           </div>
           <span className={`text-xs font-mono font-bold ${isHighRisk ? 'text-rose-500' : 'text-white'}`}>
             {val.toFixed(0)}%
           </span>
        </div>
        <div className="h-1 w-full bg-white/5 rounded-full overflow-hidden">
           <div 
             className={`h-full transition-all duration-[2000ms] ${isHighRisk ? 'bg-rose-500 shadow-[0_0_10px_#f43f5e]' : 'bg-zinc-700'}`} 
             style={{ width: `${val}%` }} 
           />
        </div>
      </div>
    );
  };

  if (!candidate) return null;

  return (
    <div className="flex flex-col w-full h-full gap-8 animate-in fade-in zoom-in-95 duration-1000 pb-8">
      
      {/* 🚀 THE COLLISION WEB CANVAS */}
      <div className="w-full flex-1 relative bg-white/[0.01] border border-white/[0.04] rounded-[4rem] p-12 flex items-center justify-center overflow-hidden shadow-[inset_0_2px_40px_rgba(0,0,0,0.7)] group/radar min-h-[450px]">
        
        {/* Background Decorative Layer */}
        <div className="absolute inset-0 opacity-[0.02] pointer-events-none flex items-center justify-center">
            <Hash size={400} className="text-white" strokeWidth={0.5} />
        </div>

        <div className="absolute top-10 left-12 flex items-center gap-4">
           <div className="p-2 bg-brand-green/10 rounded-lg border border-brand-green/20">
              <Target size={18} className="text-brand-green animate-pulse" />
           </div>
           <div className="flex flex-col">
              <span className="text-[10px] font-black uppercase tracking-[0.5em] text-white italic">Neural Topology Analysis</span>
              <span className="text-[7px] font-mono text-zinc-600 uppercase tracking-widest">Collision Protocol Active // Ver 16.3</span>
           </div>
        </div>

        <ResponsiveContainer width="100%" height="100%">
          <RadarChart cx="50%" cy="50%" outerRadius="85%" data={processedData}>
            <PolarGrid stroke="#ffffff" strokeOpacity={0.05} />
            <PolarAngleAxis 
              dataKey="subject" 
              tick={{ fill: '#52525b', fontSize: 10, fontWeight: 900, letterSpacing: '0.2em' }} 
            />
            <PolarRadiusAxis domain={[0, 100]} tick={false} axisLine={false} />
            
            {/* O ALVO (Requisitos da Vaga) */}
            <Radar
              name="Target"
              dataKey="target"
              stroke="#ffffff"
              strokeOpacity={0.1}
              fill="#ffffff"
              fillOpacity={0.03}
              strokeWidth={1}
            />

            {/* O ATIVO (Dados do Candidato) */}
            <Radar
              name="Asset"
              dataKey="candidate"
              stroke={color}
              fill={color}
              fillOpacity={0.2}
              strokeWidth={3}
              animationBegin={400}
              animationDuration={2500}
            />
          </RadarChart>
        </ResponsiveContainer>
      </div>

      {/* 📊 CRITICAL RISK MATRIX */}
      <div className="grid grid-cols-4 gap-4 shrink-0">
        <RiskBadge label="TECH_GAP" value={candidate.technicalGapRisk || 0} icon={Zap} />
        <RiskBadge label="SENIORITY" value={candidate.seniorityMismatchRisk || 0} icon={Maximize2} />
        <RiskBadge label="DRIFT" value={candidate.contextDriftRisk || 0} icon={Activity} />
        <RiskBadge label="SCALABILITY" value={candidate.scalabilityRisk || 0} icon={ShieldCheck} />
      </div>

      {/* 🧠 NEURAL VERDICT CARD - Exibe a justificativa que extraímos nos logs */}
      {candidate.whyNotHundred && (
        <div className="bg-gradient-to-br from-brand-green/10 via-black/20 to-transparent border border-brand-green/20 p-6 rounded-[2rem] shadow-2xl transition-all hover:border-brand-green/40 group/verdict">
          <div className="flex items-start gap-4">
            <div className="mt-1 p-2 bg-brand-green/20 rounded-xl border border-brand-green/30 group-hover/verdict:scale-110 transition-transform">
               <MessageSquareQuote size={18} className="text-brand-green" />
            </div>
            <div className="flex flex-col gap-2">
              <div className="flex items-center gap-3">
                <span className="text-[10px] font-black uppercase tracking-[0.3em] text-brand-green">Neural Verdict</span>
                <div className="h-[1px] flex-1 bg-brand-green/20" />
              </div>
              <p className="text-sm text-zinc-300 leading-relaxed font-medium italic">
                "{candidate.whyNotHundred}"
              </p>
              <div className="flex items-center gap-2 mt-2 opacity-40">
                <div className="h-1 w-1 rounded-full bg-brand-green" />
                <span className="text-[8px] font-mono text-zinc-400 uppercase tracking-tighter">
                  Decision support data // Recalibrated for current mission context
                </span>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}