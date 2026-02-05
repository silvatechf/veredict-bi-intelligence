'use client';

import React, { useMemo } from 'react';
import { ShieldCheck, Activity, SearchCode, Terminal } from 'lucide-react';

interface StrategicRoadmapProps {
  questions: string[] | string;
  aiInsights: {
    technicalGapInsight?: string;
    seniorityInsight?: string;
    contextDriftInsight?: string;
    scalabilityInsight?: string;
  };
}

export default function StrategicRoadmap({ questions, aiInsights }: StrategicRoadmapProps) {
  
  // 🧠 PARSER: Consolida a trindade de ferro
  const questionsList = useMemo(() => {
    if (!questions) return [];
    const rawList = typeof questions === 'string' ? questions.split('\n') : questions;
    return rawList
      .map(q => String(q).trim().replace(/^[0-9.\-\s]+/, ''))
      .filter(q => q.length > 15)
      .slice(0, 3);
  }, [questions]);

  // 🛰️ NODE GENERATOR: Conecta o Roadmap aos vereditos dinâmicos da IA
  const renderNodes = useMemo(() => {
    // Aqui mapeamos as chaves reais que a IA preenche após a análise
    const insightPool = [
      { 
        label: "TECH_GAP_ANALYSIS", 
        text: aiInsights?.technicalGapInsight, 
        risk: 'text-brand-green' 
      },
      { 
        label: "SENIORITY_AUDIT", 
        text: aiInsights?.seniorityInsight, 
        risk: 'text-amber-500' 
      },
      { 
        label: "SCALABILITY_VERIFICATION", 
        text: aiInsights?.scalabilityInsight || aiInsights?.contextDriftInsight, 
        risk: 'text-purple-500' 
      }
    ].filter(i => i.text); // Apenas eixos com dados reais aparecem

    return questionsList.map((question, index) => {
      const meta = insightPool[index] || { 
        label: "STRATEGIC_ALIGNMENT", 
        text: "Evaluation of behavioral implementation trade-offs and mission fit.",
        risk: 'text-zinc-600'
      };
      return { 
        questionText: question, 
        focusLabel: meta.label, 
        focusText: meta.text, 
        riskClass: meta.risk 
      };
    });
  }, [questionsList, aiInsights]);

  if (questionsList.length === 0) return null;

  return (
    <div className="mt-12 space-y-10 animate-in fade-in slide-in-from-bottom-10 duration-1000 pb-20">
      
      {/* 🟢 HEADER: INVESTIGATION PROTOCOL */}
      <div className="flex items-center justify-between border-b border-white/[0.05] pb-10">
        <div className="flex items-center gap-6">
          <div className="w-16 h-16 bg-black border border-white/10 rounded-[2rem] flex items-center justify-center shadow-2xl relative group">
            <div className="absolute inset-0 bg-brand-green/5 blur-xl group-hover:bg-brand-green/10 transition-all" />
            <SearchCode size={28} className="text-brand-green relative z-10" strokeWidth={1.5} />
          </div>
          <div>
            <div className="flex items-center gap-2 mb-2">
              <Terminal size={10} className="text-zinc-700" />
              <h3 className="text-[9px] font-black uppercase tracking-[0.5em] text-zinc-600 font-mono italic">Neural_Inquest_v1.7</h3>
            </div>
            <h2 className="text-4xl font-black text-white italic tracking-tighter uppercase leading-none">
              Strategic <span className="text-brand-green">Roadmap</span>
            </h2>
          </div>
        </div>
        <div className="hidden md:flex px-6 py-3 rounded-full bg-white/[0.02] border border-white/5 items-center gap-4 backdrop-blur-3xl">
          <Activity size={14} className="text-brand-green animate-pulse" />
          <span className="text-[10px] font-mono font-bold text-zinc-500 uppercase tracking-widest">
             {renderNodes.length} ACTIVE_AUDIT_NODES
          </span>
        </div>
      </div>

      {/* 🛰️ AUDIT NODES */}
      <div className="grid grid-cols-1 gap-8">
        {renderNodes.map((node, index) => (
          <div key={index} className="group relative">
            <div className="p-10 lg:p-12 bg-zinc-950/50 border border-white/[0.03] rounded-[3.5rem] hover:border-brand-green/20 transition-all duration-700 backdrop-blur-md shadow-2xl">
              <div className="flex flex-col gap-10">
                
                {/* Principal Question */}
                <div className="flex items-start gap-8">
                  <div className="shrink-0 w-14 h-14 rounded-2xl bg-black border border-white/5 flex items-center justify-center group-hover:border-brand-green/40 transition-all shadow-inner">
                    <span className="text-lg font-mono font-black text-brand-green/40 group-hover:text-brand-green transition-colors">
                      {(index + 1).toString().padStart(2, '0')}
                    </span>
                  </div>
                  <p className="text-2xl text-zinc-100 leading-tight font-bold tracking-tight pt-2 group-hover:text-white transition-colors">
                    {node.questionText}
                  </p>
                </div>
                
                {/* 🛡️ INSIGHT BOX: Dinâmico e Soberano */}
                <div className="p-10 rounded-[3rem] bg-black/40 border border-white/[0.02] flex gap-8 items-start group-hover:bg-black/60 transition-all">
                  <div className="mt-1 p-3 bg-zinc-900 rounded-xl border border-white/5 shrink-0">
                    <ShieldCheck size={22} className="text-brand-green/50 group-hover:text-brand-green transition-colors" />
                  </div>
                  <div className="space-y-4">
                    <span className={`text-[10px] font-mono font-black uppercase tracking-[0.5em] ${node.riskClass} opacity-80`}>
                      {node.focusLabel}
                    </span>
                    <p className="text-lg text-zinc-400 leading-relaxed font-serif italic opacity-90 first-letter:text-2xl first-letter:font-black first-letter:mr-1">
                      "{node.focusText}"
                    </p>
                  </div>
                </div>

                <div className="flex justify-end items-center gap-3 opacity-20 group-hover:opacity-100 transition-all duration-1000">
                   <div className="h-px w-8 bg-brand-green/20" />
                   <span className="text-[8px] font-mono text-brand-green uppercase tracking-[0.4em]">Audit_Integrity_Verified</span>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}