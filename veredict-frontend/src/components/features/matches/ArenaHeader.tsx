'use client';

import React from 'react';
import { JobOrder } from '@/types/job';
import { ChevronDown, Cpu, Radio, Target, Zap } from 'lucide-react';

interface ArenaHeaderProps {
  jobs: JobOrder[];
  selectedJobId: number | null;
  onJobChange: (id: number) => void;
}

const ArenaHeader: React.FC<ArenaHeaderProps> = ({ jobs, selectedJobId, onJobChange }) => {
  
  const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const val = e.target.value;
    const numericId = Number(val);
    if (val && !isNaN(numericId)) {
      onJobChange(numericId);
    }
  };

  const currentJob = jobs.find(j => j.id === selectedJobId);
  const hasJobs = jobs.length > 0;

  return (
    // 👁️ Aumentei bg-white/[0.01] para [0.03] e border-white/5 para /10 para dar contraste
    <div className="flex items-center gap-4 lg:gap-6 bg-white/[0.03] backdrop-blur-2xl p-1.5 pl-4 lg:pl-6 rounded-full border border-white/10 shadow-2xl transition-all duration-700 group/header hover:border-brand-green/20 relative z-[60]">
      <div className="flex items-center gap-4 lg:gap-5">
        
        {/* 🧠 NEURAL INDICATOR */}
        <div className="relative shrink-0">
          <div className={`p-2.5 lg:p-3 rounded-xl transition-all duration-700 relative z-10 border ${
            selectedJobId 
              ? 'bg-brand-green text-black border-brand-green/40 shadow-[0_0_25px_-5px_#10b981]' 
              : 'bg-zinc-900/50 text-zinc-500 border-white/5'
          }`}>
            <Cpu 
              size={16} 
              strokeWidth={2.5} 
              className={`${selectedJobId ? 'animate-[spin_8s_linear_infinite]' : ''} w-3.5 h-3.5 lg:w-4 lg:h-4`} 
            />
          </div>
          {selectedJobId && (
            <div className="absolute inset-0 bg-brand-green/20 blur-xl animate-pulse rounded-full" />
          )}
        </div>
        
        <div className="flex flex-col min-w-[180px] md:min-w-[280px] lg:min-w-[320px]">
          {/* Status Bar */}
          <div className="flex items-center gap-2 mb-0.5 opacity-60">
            <div className={`w-1 h-1 rounded-full ${selectedJobId ? 'bg-brand-green animate-pulse' : 'bg-zinc-700'}`} />
            <span className="text-[7px] font-mono font-black text-zinc-400 uppercase tracking-[0.3em] italic">
              {hasJobs ? `MISSION_AXIS_ORD_0${selectedJobId || '??'}` : 'SYSTEM_IDLE'}
            </span>
          </div>

          <div className="relative flex items-center group/select">
            <select 
              value={selectedJobId || ''}
              onChange={handleChange}
              className="bg-transparent text-sm lg:text-base font-black text-white appearance-none pr-8 outline-none cursor-pointer hover:text-brand-green transition-all uppercase tracking-tighter italic font-sans w-full relative z-10 disabled:cursor-not-allowed"
              disabled={!hasJobs}
            >
              {!hasJobs ? (
                <option value="" className="bg-[#0a0a0a] text-zinc-600">AWAITING_INITIALIZATION...</option>
              ) : (
                <>
                  <option value="" disabled className="bg-[#0a0a0a] text-zinc-600">SELECT_ACTIVE_MISSION</option>
                  {jobs.map(job => (
                    <option key={job.id} value={job.id} className="bg-[#0a0a0a] text-zinc-300 font-mono text-xs">
                      {`NODE_${job.id} // ${job.title?.toUpperCase() || "UNNAMED"}`}
                    </option>
                  ))}
                </>
              )}
            </select>
            <ChevronDown size={12} className={`absolute right-0 pointer-events-none z-20 transition-all ${selectedJobId ? 'text-brand-green' : 'text-zinc-500'}`} />
            
            {/* 🟢 Underline Tático */}
            <div className={`absolute bottom-[-2px] left-0 h-[1px] transition-all duration-1000 ${
                selectedJobId ? 'bg-brand-green/40 w-full shadow-[0_0_8px_#10b981]' : 'bg-white/10 w-0'
            }`} />
          </div>
        </div>
      </div>
      
      {/* 📡 SIGNAL INTELLIGENCE */}
      <div className="pr-6 lg:pr-8 border-l border-white/10 pl-4 lg:pl-6 hidden md:block">
          <div className="flex items-center gap-6">
            <div className="flex flex-col gap-1">
                <div className="flex items-center gap-2 opacity-70">
                  <Radio size={8} className={selectedJobId ? 'text-brand-green' : 'text-zinc-600'} />
                  <span className="text-[8px] font-mono font-black text-zinc-400 uppercase tracking-[0.2em]">
                      {selectedJobId ? 'LINK_ESTABLISHED' : 'SIGNAL_LOSS'}
                  </span>
                </div>
                
                <div className="flex items-center gap-2">
                  <Target size={8} className={selectedJobId ? 'text-brand-green/60' : 'text-zinc-800'} />
                  <span className="text-[7px] text-zinc-400 font-mono font-bold uppercase tracking-widest truncate max-w-[120px]">
                      {selectedJobId ? `${currentJob?.companyName || 'VEREDICT_BI'}` : 'NO_COORDINATES'}
                  </span>
                </div>
            </div>

            {selectedJobId && (
              <div className="flex flex-col items-center justify-center pl-4 lg:pl-6 border-l border-white/10 opacity-40 group-hover/header:opacity-80 transition-opacity">
                <Zap size={10} className="text-brand-green" />
                <span className="text-[6px] font-mono font-black text-zinc-500 uppercase tracking-widest">SOVEREIGN</span>
              </div>
            )}
          </div>
      </div>
    </div>
  );
};

export default ArenaHeader;