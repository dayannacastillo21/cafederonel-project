import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

export type PdfTableExportOptions = {
  filename: string;
  title: string;
  subtitle?: string;
  headers: string[];
  rows: string[][];
};

export function downloadPdfTable(options: PdfTableExportOptions): void {
  const doc = new jsPDF({ orientation: 'landscape', unit: 'pt', format: 'a4' });
  const margin = 40;
  let cursorY = margin;

  doc.setFont('helvetica', 'bold');
  doc.setFontSize(16);
  doc.text(options.title, margin, cursorY);
  cursorY += 22;

  if (options.subtitle) {
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(10);
    const lines = doc.splitTextToSize(options.subtitle, doc.internal.pageSize.getWidth() - margin * 2);
    doc.text(lines, margin, cursorY);
    cursorY += lines.length * 14 + 8;
  }

  autoTable(doc, {
    startY: cursorY,
    head: [options.headers],
    body: options.rows,
    styles: { fontSize: 8, cellPadding: 4 },
    headStyles: { fillColor: [189, 132, 41], textColor: 255 },
    margin: { left: margin, right: margin },
  });

  doc.save(options.filename.endsWith('.pdf') ? options.filename : `${options.filename}.pdf`);
}
