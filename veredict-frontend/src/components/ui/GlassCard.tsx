'use client';

import { ReactNode } from 'react';

interface GlassCardProps {
  children: ReactNode;
  className?: string;
  hoverEffect?: boolean;
}

export default function GlassCard({ 
  children, 
  className = "", 
  hoverEffect = true 
}: GlassCardProps) {
  return (
    <div className={`
      glass-card 
      relative 
      overflow-hidden 
      ${hoverEffect ? 'glass-card-hover' : ''} 
      ${className}
    `}>
      {/* Sutil gradiente interno para dar profundidade de "vidro real" */}
      <div className="absolute inset-0 bg-gradient-to-br from-white/[0.02] to-transparent pointer-events-none" />
      
      <div className="relative z-10">
        {children}
      </div>
    </div>
  );
}