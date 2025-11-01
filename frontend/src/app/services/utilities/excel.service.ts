import { Injectable } from '@angular/core';
import * as XLSX from 'xlsx';
import { ReporteData } from '../../interfaces/reporte.interface';

@Injectable({ providedIn: 'root' })
export class ExcelExportService {
  exportarReportes(reportes: ReporteData[]): void {
    const data = reportes.map(r => ({
      Vendedor: r.vendedor,
      Producto: r.producto,
      Zona: r.zona,
      Meta: r.meta,
      Ventas: r.ventas,
      '% Meta': `${r.porcentajeMeta}%`,
      Periodo: r.periodo
    }));

    const worksheet = XLSX.utils.json_to_sheet(data);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Reportes');

    XLSX.writeFile(workbook, 'reportes.xlsx');
  }
}
