import { Injectable } from '@angular/core';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { ReporteData } from '../../interfaces/reporte.interface';

@Injectable({ providedIn: 'root' })
export class PdfExportService {
  exportarReportes(reportes: ReporteData[]): void {
    const doc = new jsPDF('l', 'mm', 'a4');
    doc.setFontSize(14);
    doc.text('Reporte de Ventas', 14, 15);

    const body = reportes.map(r => [
      r.vendedor,
      r.producto,
      r.zona,
      r.meta,
      r.ventas,
      `${r.porcentajeMeta}%`,
      r.periodo
    ]);

    autoTable(doc, {
      head: [['Vendedor', 'Producto', 'Zona', 'Meta', 'Ventas', '% Meta', 'Periodo']],
      body,
      startY: 25,
      styles: { fontSize: 9, cellPadding: 3 },
      headStyles: { fillColor: [40, 116, 166] },
    });

    doc.save('reportes.pdf');
  }
}
