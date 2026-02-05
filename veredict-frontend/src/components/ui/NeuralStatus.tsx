'use client';

import { Zap, Activity } from 'lucide-react';

interface NeuralStatusProps {
  isSyncing: boolean;
  nodeCount: number;
}

export default function NeuralStatus({ isSyncing, nodeCount }: NeuralStatusProps) {
  return (
    <div className="flex items-center gap-8 border-r border-white/5 pr-8 hidden lg:flex select-none">
      
      {/* 📡 TELEMETRY 01: NEURAL STREAM STATUS */}
      <div className="flex flex-col items-end">
        <span className="text-[7px] text-zinc-600 uppercase font-black tracking-[0.3em] mb-1 italic">Neural Stream</span>
        <div className="flex items-center gap-2">
          <div className="relative flex items-center justify-center">
            <div className={`w-1 h-1 rounded-full transition-colors duration-500 ${isSyncing ? 'bg-brand-green' : 'bg-zinc-800'}`} />
            {isSyncing && (
              <div className="absolute w-3 h-3 rounded-full border border-brand-green/40 animate-ping" />
            )}
          </div>
          <span className={`text-[9px] font-bold font-mono tracking-tighter transition-colors duration-500 ${isSyncing ? 'text-brand-green' : 'text-zinc-500'}`}>
            {isSyncing ? 'SYNCING_NODES...' : 'MATRIX_STABLE'}
          </span>
        </div>
      </div>

      {/* ⚡ TELEMETRY 02: ASSET DENSITY MATRIX */}
      <div className="flex flex-col items-end">
        <span className="text-[7px] text-zinc-600 uppercase font-black tracking-[0.3em] mb-1 italic">Active Assets</span>
        <div className="flex items-center gap-2">
          <Zap 
            size={10} 
            className={`transition-all duration-700 ${isSyncing ? 'animate-pulse text-brand-green' : 'text-zinc-700'}`} 
            fill={isSyncing ? 'currentColor' : 'none'}
          />
          <span className={`text-[11px] font-[1000] font-mono transition-colors duration-500 ${nodeCount > 0 ? 'text-white' : 'text-zinc-800'}`}>
            {nodeCount.toString().padStart(2, '0')}
          </span>
          {/* Micro-indicador de atividade */}
          <Activity size={8} className={`text-zinc-800 transition-opacity ${isSyncing ? 'opacity-100' : 'opacity-0'}`} />
        </div>
      </div>
    </div>
  );
}