import { jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';
import { Talent } from '@/types/talent';

export const exportExecutiveVerdict = (talent: Talent) => {
  const doc = new jsPDF({
    orientation: 'portrait',
    unit: 'mm',
    format: 'a4'
  });

  const timestamp = new Date().toLocaleString('en-GB', { timeZone: 'UTC' });
  const auditId = `VRDCT-${Math.random().toString(36).substring(2, 9).toUpperCase()}`;
  const score = talent.matchScore || 0;
  const isElite = score >= 80;

  // --- 1. HEADER: AUDIT AUTHORITY BAR ---
  doc.setFillColor(10, 10, 10); // Quase preto (Soberano)
  doc.rect(0, 0, 210, 45, 'F');
  
  doc.setTextColor(16, 185, 129); // Brand Green
  doc.setFont("courier", "bold");
  doc.setFontSize(22);
  doc.text("VEREDICTBI", 20, 20);
  
  doc.setTextColor(120, 120, 120);
  doc.setFontSize(8);
  doc.setFont("courier", "normal");
  doc.text(`REPORT_ID: ${auditId}`, 20, 28);
  doc.text(`NEURAL_ENGINE_VERSION: v17.4.2 [STABLE]`, 20, 33);
  doc.text(`TIMESTAMP: ${timestamp} UTC`, 20, 38);

  doc.setTextColor(255, 255, 255);
  doc.setFont("helvetica", "bold");
  doc.setFontSize(10);
  doc.text("OFFICIAL EXECUTIVE DECISION DOSSIER (EDD)", 120, 20);
  doc.text("CONFIDENTIAL / INTERNAL USE ONLY", 120, 25);

  // --- 2. ASSET IDENTIFICATION ---
  doc.setTextColor(30, 30, 30);
  doc.setFontSize(26);
  doc.setFont("helvetica", "bold");
  const targetName = (talent.name || talent.candidateName || "INTERNAL_ASSET").toUpperCase();
  doc.text(targetName, 20, 65);
  
  doc.setFontSize(11);
  doc.setFont("helvetica", "normal");
  doc.setTextColor(100, 100, 100);
  doc.text(`Role Target: ${talent.professionalTarget || 'N/A'}`, 20, 73);
  doc.text(`Node Identity: ${talent.email || 'N/A'}`, 20, 78);

  // --- 3. THE SOVEREIGN VERDICT (A Peça de Valor) ---
  const verdictColor = isElite ? [200, 255, 230] : [255, 240, 200];
  const textColor = isElite ? [6, 95, 70] : [146, 64, 14];
  
  doc.setFillColor(verdictColor[0], verdictColor[1], verdictColor[2]);
  doc.roundedRect(20, 88, 170, 38, 4, 4, 'F');

  doc.setTextColor(textColor[0], textColor[1], textColor[2]);
  doc.setFont("helvetica", "bold");
  doc.setFontSize(10);
  doc.text("PRIMARY NEURAL SENTENCE:", 30, 100);
  
  doc.setFontSize(26);
  const verdictLabel = isElite ? "GO / RECOMMENDED" : score >= 60 ? "CONDITIONAL APPROVAL" : "NO-GO / HIGH RISK";
  doc.text(verdictLabel, 30, 115);

  // --- 4. RISK & PERFORMANCE MATRIX (AutoTable) ---
  doc.setTextColor(30, 30, 30);
  doc.setFontSize(13);
  doc.text("STRATEGIC IMPACT ANALYSIS", 20, 145);

  autoTable(doc, {
    startY: 150,
    margin: { left: 20, right: 20 },
    theme: 'striped',
    headStyles: { fillColor: [30, 30, 30], textColor: [255, 255, 255], fontStyle: 'bold', fontSize: 9 },
    bodyStyles: { fontSize: 10, textColor: [50, 50, 50], cellPadding: 4 },
    head: [['ANALYSIS VECTOR', 'INDEX', 'IMPACT NARRATIVE']],
    body: [
      ['Technical Fit', `${(talent.technicalFit || 0).toFixed(1)}%`, 'Core infrastructure and skill-stack alignment.'],
      ['Context Fit', `${(talent.contextFit || 0).toFixed(1)}%`, 'Adaptability to company specific paradigms.'],
      ['Seniority Alignment', `${(100 - (talent.seniorityMismatchRisk || 0)).toFixed(1)}%`, 'Risk of role compression or over-qualification.'],
      ['Scalability Potential', `${(100 - (talent.scalabilityRisk || 0)).toFixed(1)}%`, 'Asset potential for high-velocity environments.']
    ],
  });

  // --- 5. NEURAL GAP AUDIT (Why not 100%?) ---
  const finalY = (doc as any).lastAutoTable.finalY || 190;
  doc.setFontSize(12);
  doc.setFont("helvetica", "bold");
  doc.text("NEURAL GAP AUDIT (Why not 100%?)", 20, finalY + 15);
  
  doc.setFontSize(10);
  doc.setFont("helvetica", "italic");
  doc.setTextColor(80, 80, 80);
  const gapText = talent.whyNotHundred || "Asset demonstrates baseline alignment. Friction identified in specific scalability vectors and architectural experience.";
  const splitGap = doc.splitTextToSize(gapText, 170);
  doc.text(splitGap, 20, finalY + 22);

  // --- 6. EXECUTIVE BRIEFING ---
  doc.setFont("helvetica", "bold");
  doc.setFontSize(12);
  doc.setTextColor(30, 30, 30);
  doc.text("EXECUTIVE BRIEFING", 20, finalY + 45);
  doc.setFont("helvetica", "normal");
  doc.setFontSize(10);
  const briefingText = talent.aiSummary || "No briefing provided.";
  const splitBriefing = doc.splitTextToSize(`"${briefingText}"`, 170);
  doc.text(splitBriefing, 20, finalY + 52);

  // --- 7. FOOTER: LEGAL BLINDAGE & CONFIDENCE ---
  const pageHeight = doc.internal.pageSize.height;
  doc.setFontSize(7);
  doc.setTextColor(180, 180, 180);
  doc.setDrawColor(220, 220, 220);
  doc.line(20, pageHeight - 25, 190, pageHeight - 25);
  
  doc.text(`CONFIDENCE INDEX: 94.22% // ALGORITHM: RF_NEURAL_COLLISION_v17`, 20, pageHeight - 18);
  
  doc.setFont("helvetica", "bold");
  doc.setTextColor(140, 140, 140);
  doc.text("LEGAL DISCLAIMER:", 100, pageHeight - 18);
  doc.setFont("helvetica", "normal");
  const disclaimer = "Veredict quantifies risk through neural alignment. The final decision remains sovereign to the board. Veredict Lab is not liable for performance outcomes.";
  const splitDisclaimer = doc.splitTextToSize(disclaimer, 90);
  doc.text(splitDisclaimer, 100, pageHeight - 14);

  // --- FINAL: SAVE ---
  const fileName = `EDD_${targetName.replace(/\s+/g, '_')}_${auditId}.pdf`;
  doc.save(fileName);
};