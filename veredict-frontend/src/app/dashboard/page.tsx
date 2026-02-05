'use client';

import { useState, useEffect, useMemo, useCallback, useRef } from 'react';
import { talentService, Talent } from '@/services/talentService';
import { jobService } from '@/services/jobService';
import { JobOrder } from '@/types/job';

import AuditInventory from '@/components/dashboard/AuditInventory';
import ExecutiveDossier from '@/components/dashboard/ExecutiveDossier';
import StrategicRoadmap from '@/components/dashboard/StrategicRoadmap';
import TopologyRadar from '@/components/shared/charts/TopologyRadar';
import ArenaHeader from '@/components/features/matches/ArenaHeader';
import NeuralStatus from '@/components/ui/NeuralStatus';

import { 
  RefreshCcw, Edit3, Save, FileUp, Terminal, Activity, 
  ShieldCheck, Plus, X, Trash2, Building2, 
  ChevronDown, ChevronUp 
} from 'lucide-react';
import { toast } from 'sonner';

export default function DashboardPage() {
  const [talents, setTalents] = useState<Talent[]>([]);
  const [jobs, setJobs] = useState<JobOrder[]>([]);
  const [selectedJobId, setSelectedJobId] = useState<number | null>(null);
  const [activeTalentId, setActiveTalentId] = useState<number | null>(null);
  const [isSyncing, setIsSyncing] = useState(false);
  const [isEditingJob, setIsEditingJob] = useState(false);
  const [isDescriptionExpanded, setIsDescriptionExpanded] = useState(false);
  
  const [editedDescription, setEditedDescription] = useState('');
  const [editedCompany, setEditedCompany] = useState('');
  const [editedTitle, setEditedTitle] = useState('');
  
  const fileInputRef = useRef<HTMLInputElement>(null);

  const selectedJob = useMemo(() => jobs.find(j => j.id === selectedJobId) || null, [jobs, selectedJobId]);
  const activeTalent = useMemo(() => talents.find(t => t.id === activeTalentId) || null, [talents, activeTalentId]);

  useEffect(() => {
    if (selectedJob) {
      setEditedDescription(selectedJob.description || '');
      setEditedCompany(selectedJob.companyName || '');
      setEditedTitle(selectedJob.title || '');
    }
  }, [selectedJobId, selectedJob]);

  // 🧠 FETCH: Sincronia de Nodes
  const fetchTalents = useCallback(async (jobId: number) => {
    setIsSyncing(true);
    try {
      const data = await talentService.getRankedMatches(jobId, 1);
      const cleanData = data || [];
      setTalents(cleanData);
      
      if (cleanData.length > 0) {
        setActiveTalentId(cleanData[0].id);
      } else {
        setActiveTalentId(null);
      }
    } catch (e) {
      toast.error("VeredictBI Link Failure.");
    } finally {
      setIsSyncing(false);
    }
  }, []);

  const handleDeleteMission = async () => {
    if (!selectedJobId) return;
    if (!confirm("CRITICAL: Permanent destruction of mission data?")) return;
    setIsSyncing(true);
    try {
      await jobService.deleteJob(selectedJobId);
      const updatedJobs = jobs.filter(j => j.id !== selectedJobId);
      setJobs(updatedJobs);
      toast.success("Mission purged.");
      if (updatedJobs.length > 0) handleJobChange(updatedJobs[0].id);
      else startNewMission();
    } catch (error) {
      toast.error("Purge failure.");
    } finally {
      setIsSyncing(false);
    }
  };

  // 🚨 AJUSTE TÁTICO: Reset Total de Estados para evitar Match % estático
  const handleJobChange = (id: number) => {
    setIsSyncing(true);
    setTalents([]); // Limpa a lista imediatamente para feedback visual
    setActiveTalentId(null);
    setSelectedJobId(id);
    setIsDescriptionExpanded(false); 
    fetchTalents(id);
  };

  const startNewMission = () => {
    setSelectedJobId(null);
    setTalents([]);
    setActiveTalentId(null);
    setEditedDescription('');
    setEditedCompany('');
    setEditedTitle('');
    setIsEditingJob(true);
  };

  const handleUpdateJobContext = async () => {
    if (!editedDescription.trim() || !editedTitle.trim()) {
      toast.error("Required: Title & DNA Briefing.");
      return;
    }
    setIsSyncing(true);
    try {
      const payload: any = { 
        description: editedDescription,
        companyName: editedCompany || "Private Client",
        title: editedTitle,
        seniority: "SENIOR",
        status: "OPEN"
      };
      if (selectedJobId) payload.id = selectedJobId;
      const updated = await jobService.createJob(payload);
      if (updated) {
        setJobs(prev => selectedJobId ? prev.map(j => j.id === selectedJobId ? updated : j) : [updated, ...prev]);
        setSelectedJobId(updated.id); 
        setIsEditingJob(false);
        await fetchTalents(updated.id);
        toast.success("Strategic Context Deployed.");
      }
    } catch (error) {
      toast.error("Deployment failure.");
    } finally {
      setIsSyncing(false);
    }
  };

  useEffect(() => {
    async function init() {
      try {
        const data = await jobService.getAllJobs();
        setJobs(data || []);
        if (data && data.length > 0) {
          const firstJobId = data[0].id;
          setSelectedJobId(firstJobId);
          fetchTalents(firstJobId);
        } else {
          setIsEditingJob(true);
        }
      } catch (e) {
        toast.error("VeredictBI Core Offline.");
      }
    }
    init();
  }, [fetchTalents]);

  return (
    <div className="flex flex-col h-screen overflow-hidden bg-[#020202] text-zinc-400 font-sans tracking-tight">
      
      {/* 🟢 NAVIGATION */}
      <nav className="h-16 shrink-0 bg-black/40 border-b border-white/[0.03] flex items-center justify-between px-10 z-[100] backdrop-blur-3xl">
        <div className="flex items-center gap-6">
           <div className="flex items-center gap-2">
             <div className="w-6 h-6 bg-brand-green rounded flex items-center justify-center shadow-[0_0_15px_rgba(16,185,129,0.4)]">
               <ShieldCheck size={14} className="text-black" />
             </div>
             <span className="text-sm font-black text-white italic uppercase tracking-tighter">Veredict<span className="text-brand-green">BI</span></span>
           </div>
           <ArenaHeader jobs={jobs} selectedJobId={selectedJobId} onJobChange={handleJobChange} />
           <button onClick={startNewMission} className="flex items-center gap-2 px-5 py-2 bg-brand-green/10 border border-brand-green/20 rounded-full text-[9px] font-black text-brand-green hover:bg-brand-green hover:text-black transition-all uppercase tracking-widest group">
             <Plus size={12} className="group-hover:rotate-90 transition-transform" /> New Mission
           </button>
        </div>
        <div className="flex items-center gap-8">
           <NeuralStatus isSyncing={isSyncing} nodeCount={talents.length} />
           <div className="flex items-center gap-2">
             {selectedJobId && (
               <button onClick={handleDeleteMission} className="p-2 hover:bg-rose-500/10 rounded-full transition-all group" title="Purge Mission">
                 <Trash2 size={14} className="text-zinc-700 group-hover:text-rose-500" />
               </button>
             )}
             <button onClick={() => selectedJobId && fetchTalents(selectedJobId)} className="p-2 hover:bg-white/5 rounded-full transition-all">
               <RefreshCcw size={14} className={isSyncing ? 'animate-spin text-brand-green' : 'text-zinc-600'} />
             </button>
           </div>
        </div>
      </nav>

      <main className="flex-1 flex overflow-hidden p-8 gap-8 bg-[radial-gradient(circle_at_50%_0%,_rgba(16,185,129,0.03)_0%,_transparent_70%)]">
        
        {/* 1. SIDEBAR */}
        <aside className="w-[360px] flex flex-col gap-8 shrink-0 h-full">
          <div className="flex-1 glass-card rounded-[3rem] overflow-hidden flex flex-col border-white/[0.02] shadow-2xl relative">
             <div className="p-8 border-b border-white/[0.05] bg-white/[0.01] flex justify-between items-center">
                <div className="flex items-center gap-3">
                   <Terminal size={12} className="text-zinc-600" />
                   <span className="text-[10px] font-black uppercase tracking-[0.4em] text-zinc-500 italic">Verified_Nodes</span>
                </div>
                <span className="text-brand-green text-xs font-mono font-bold">[{talents.length}]</span>
             </div>
             <div className="flex-1 overflow-y-auto p-4 custom-scrollbar">
                {/* 🚨 KEY ATTACHED: Força o componente a resetar as porcentagens ao mudar a vaga */}
                <AuditInventory 
                  key={selectedJobId || 'idle'}
                  talents={talents} 
                  onSelect={(t) => setActiveTalentId(t.id)} 
                  selectedIds={activeTalent ? [activeTalent.id] : []} 
                  onDeleteSuccess={() => selectedJobId && fetchTalents(selectedJobId)} 
                />
             </div>
          </div>
          <button onClick={() => fileInputRef.current?.click()} className="h-24 bg-brand-green hover:brightness-110 text-black rounded-[3rem] flex flex-col items-center justify-center transition-all shadow-[0_25px_60_px_-20px_rgba(16,185,129,0.5)] shrink-0">
            <FileUp size={24} className="mb-1" />
            <span className="text-[11px] font-[1000] uppercase tracking-widest font-mono">Ingest Asset DNA</span>
            <input type="file" ref={fileInputRef} className="hidden" onChange={(e) => {
                const file = e.target.files?.[0];
                if(file && selectedJobId) {
                  const fd = new FormData(); fd.append('file', file);
                  talentService.uploadCandidates(fd).then(() => {
                    toast.success("Ingestion initiated.");
                    setTimeout(() => fetchTalents(selectedJobId), 2500);
                  });
                }
              }} />
          </button>
        </aside>

        {/* 2. WORKSPACE */}
        <div className="flex-1 overflow-y-auto custom-scrollbar pr-4 space-y-8 h-full">
          
          <section className="flex-shrink-0 relative overflow-hidden rounded-[2.5rem] border border-white/[0.08] bg-[#050505] shadow-2xl z-20">
             <div className="p-10 relative z-10 flex flex-col gap-6">
                <div className="flex justify-between items-center">
                   <div className="flex items-center gap-4">
                      <div className="h-[1px] w-8 bg-brand-green/30" />
                      <span className="text-[9px] font-mono uppercase tracking-[0.5em] text-zinc-500 font-bold">Mission_Parameters</span>
                   </div>
                   {!isEditingJob && (
                     <div className="flex items-center gap-6">
                       <button onClick={() => setIsDescriptionExpanded(!isDescriptionExpanded)} className="flex items-center gap-2 text-[9px] font-black uppercase tracking-widest transition-all">
                         <span className={isDescriptionExpanded ? 'text-brand-green' : 'text-zinc-500 hover:text-zinc-300'}>
                           {isDescriptionExpanded ? "Collapse" : "Read Briefing"}
                         </span>
                         <ChevronDown size={12} className={`transition-transform duration-500 ${isDescriptionExpanded ? 'rotate-180 text-brand-green' : 'text-zinc-600'}`} />
                       </button>
                       <button onClick={() => setIsEditingJob(true)} className="p-2 text-zinc-600 hover:text-white transition-all"><Edit3 size={14} /></button>
                     </div>
                   )}
                </div>

                <div className="relative">
                   {isEditingJob ? (
                      <div className="space-y-6 animate-in fade-in duration-500">
                        <div className="grid grid-cols-2 gap-4">
                          <input value={editedTitle} onChange={(e) => setEditedTitle(e.target.value)} className="bg-zinc-900/30 border border-white/5 rounded-xl px-4 py-3 text-white outline-none focus:border-brand-green/40 text-sm" placeholder="Mission Title" />
                          <input value={editedCompany} onChange={(e) => setEditedCompany(e.target.value)} className="bg-zinc-900/30 border border-white/5 rounded-xl px-4 py-3 text-white outline-none focus:border-brand-green/40 text-sm" placeholder="Company" />
                        </div>
                        <textarea value={editedDescription} onChange={(e) => setEditedDescription(e.target.value)} className="w-full h-36 bg-zinc-900/10 border border-brand-green/20 rounded-2xl p-8 text-zinc-300 text-lg font-serif italic outline-none focus:border-brand-green/40" />
                        <div className="flex justify-end gap-4">
                          <button onClick={() => setIsEditingJob(false)} className="text-[10px] font-black uppercase text-zinc-600">Abort</button>
                          <button onClick={handleUpdateJobContext} className="bg-brand-green text-black px-8 py-2 rounded-xl text-[10px] font-black uppercase tracking-widest shadow-[0_0_20px_rgba(16,185,129,0.3)]">Deploy</button>
                        </div>
                      </div>
                   ) : (
                      <div className="flex flex-col gap-4">
                         <div className="flex items-center gap-3">
                           <span className="px-2 py-0.5 rounded bg-brand-green/10 text-[9px] font-black text-brand-green uppercase tracking-widest border border-brand-green/20">
                             {selectedJob?.companyName || "Private"}
                           </span>
                           <h1 className="text-white text-lg font-black uppercase tracking-tighter italic">
                             {selectedJob?.title || "Unnamed_Node"}
                           </h1>
                         </div>
                         <div className={`transition-all duration-700 ease-in-out overflow-hidden ${isDescriptionExpanded ? 'max-h-[1000px] opacity-100' : 'max-h-16 opacity-40 blur-[0.3px]'}`}>
                            <p className="text-zinc-400 font-serif italic text-base leading-relaxed">
                               {selectedJob?.description || "Standby for mission briefing data..."}
                            </p>
                         </div>
                      </div>
                   )}
                </div>
             </div>
          </section>

          {/* 📊 NODE ANALYSIS GRID */}
          {activeTalent && (
            <div className="grid grid-cols-12 gap-8 animate-in fade-in slide-in-from-bottom-12 duration-1000 pb-20">
              <div className="col-span-12 xl:col-span-5">
                <ExecutiveDossier talents={[activeTalent]} />
              </div>
              <div className="col-span-12 xl:col-span-7 glass-card rounded-[4rem] p-16 min-h-[600px] relative flex flex-col justify-center border-white/[0.02] shadow-2xl overflow-hidden">
                <div className="absolute top-10 left-12 opacity-20 flex items-center gap-3">
                   <Terminal size={14} className="text-brand-green" />
                   <span className="text-[10px] font-mono uppercase tracking-[0.6em] italic font-bold">Node_Topology_Audit</span>
                </div>
                <TopologyRadar candidate={activeTalent} />
              </div>
              <div className="col-span-12 mt-4">
                <StrategicRoadmap questions={activeTalent.interviewQuestions || []} aiInsights={activeTalent} />
              </div>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}