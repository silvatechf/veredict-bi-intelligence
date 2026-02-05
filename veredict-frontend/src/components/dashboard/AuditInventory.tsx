'use client';

import React, { useState, useMemo } from 'react';
import { Trash2, Loader2, Fingerprint, Zap, ShieldCheck, BarChart3, Activity } from 'lucide-react';
import { talentService, Talent } from '@/services/talentService';
import TalentDrawer from './TalentDrawer';
import { toast } from 'sonner';

interface AuditInventoryProps {
  talents: Talent[];
  onDeleteSuccess: () => void;
  onSelect: (talent: Talent) => void;
  selectedIds: number[];
}

export default function AuditInventory({ talents, onDeleteSuccess, onSelect, selectedIds }: AuditInventoryProps) {
  const [inspectingId, setInspectingId] = useState<number | null>(null);
  const [isDeleting, setIsDeleting] = useState<number | null>(null);

  const consolidatedTalents = useMemo(() => {
    const map = new Map<string, Talent>();
    talents.forEach(t => {
      const email = (t.email || t.candidateEmail || String(t.id)).toLowerCase();
      if (!map.has(email)) map.set(email, t);
    });
    return Array.from(map.values()).sort((a, b) => (b.matchScore || 0) - (a.matchScore || 0));
  }, [talents]);

  const currentTalent = consolidatedTalents.find((t) => t.id === inspectingId) || null;

  const handleDelete = async (e: React.MouseEvent, id: number) => {
    e.stopPropagation();
    if (!confirm("PROTOCOL: Purge asset from VeredictBI Arena?")) return;
    setIsDeleting(id);
    try {
      await talentService.deleteTalent(id);
      toast.success("Asset Purged.");
      onDeleteSuccess();
    } catch (error) {
      toast.error("Purge Failed.");
    } finally {
      setIsDeleting(null);
    }
  };

  if (consolidatedTalents.length === 0) return null;

  return (
    <div className="w-full space-y-3 p-1">
      {consolidatedTalents.map((talent, index) => {
        const isSelected = selectedIds.includes(talent.id);
        const score = talent.matchScore || 0;
        
        const theme = score >= 80 
          ? { color: 'text-brand-green', bg: 'bg-brand-green/5', border: 'border-brand-green/20', label: 'ELITE_NODE' }
          : { color: 'text-amber-500/80', bg: 'bg-amber-500/5', border: 'border-amber-500/10', label: 'RISK_NODE' };

        return (
          <div 
            key={talent.id} 
            onClick={() => onSelect(talent)}
            onDoubleClick={() => setInspectingId(talent.id)}
            className={`group relative p-5 rounded-[2rem] border transition-all duration-500 cursor-pointer ${
              isSelected 
                ? 'border-brand-green/30 bg-brand-green/[0.04] shadow-[0_10px_40px_-15px_rgba(16,185,129,0.1)]' 
                : 'bg-white/[0.01] border-white/[0.03] hover:border-white/10'
            }`}
          >
            {/* Index Background */}
            <div className="absolute right-6 top-1/2 -translate-y-1/2 opacity-[0.03] text-4xl font-black italic group-hover:opacity-[0.07] transition-opacity font-mono">
              {String(index + 1).padStart(2, '0')}
            </div>

            <div className="flex items-center gap-4 relative z-10">
              {/* Icon Container */}
              <div className={`w-11 h-11 rounded-2xl flex items-center justify-center border transition-all duration-500 ${
                isSelected 
                  ? 'bg-brand-green text-black border-brand-green/40' 
                  : 'bg-black text-zinc-600 border-white/5 group-hover:border-brand-green/20'
              }`}>
                {isSelected ? <BarChart3 size={18} /> : <Activity size={18} />}
              </div>
              
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 mb-1">
                  <h3 className={`text-[11px] font-[1000] uppercase truncate tracking-tight ${isSelected ? 'text-white' : 'text-zinc-400'}`}>
                    {talent.name || talent.candidateName}
                  </h3>
                </div>

                {/* Status Badge */}
                <div className={`inline-flex items-center gap-1.5 px-2 py-0.5 rounded-md border ${theme.border} ${theme.bg} ${theme.color}`}>
                  <div className={`w-1 h-1 rounded-full bg-current ${score >= 80 ? 'animate-pulse' : ''}`} />
                  <span className="text-[7px] font-black uppercase tracking-[0.2em] font-mono">
                    {theme.label} // {score.toFixed(0)}%
                  </span>
                </div>
              </div>

              {/* Action Area */}
              <button 
                disabled={isDeleting === talent.id} 
                onClick={(e) => handleDelete(e, talent.id)} 
                className={`p-2.5 rounded-xl bg-black/40 border border-white/5 text-zinc-700 hover:text-rose-500 hover:border-rose-500/20 transition-all ${isSelected ? 'opacity-100' : 'opacity-0 group-hover:opacity-100'}`}
              >
                {isDeleting === talent.id ? <Loader2 size={12} className="animate-spin" /> : <Trash2 size={12} />}
              </button>
            </div>
          </div>
        );
      })}

      <TalentDrawer 
        candidate={currentTalent} 
        isOpen={!!inspectingId} 
        onClose={() => setInspectingId(null)} 
        onRefresh={onDeleteSuccess} 
      />
    </div>
  );
}