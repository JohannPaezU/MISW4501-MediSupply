import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CsvExportService {

  constructor() { }


  exportarACsv(data: any[], filename: string, headers?: { [key: string]: string }): void {
    if (!data || data.length === 0) {
      console.error('No hay datos para exportar.');
      return;
    }

    const columnHeaders = headers ? Object.values(headers) : Object.keys(data[0]);
    const keys = headers ? Object.keys(headers) : Object.keys(data[0]);

    const csvRows = [
      columnHeaders.join(',')
    ];

    for (const row of data) {
      const values = keys.map(key => {
        const escaped = ('' + row[key]).replace(/"/g, '\\"');
        return `"${escaped}"`;
      });
      csvRows.push(values.join(','));
    }

    const csvString = csvRows.join('\n');
    const blob = new Blob([csvString], { type: 'text/csv;charset=utf-8;' });

    const link = document.createElement('a');
    if (link.download !== undefined) {
      const url = URL.createObjectURL(blob);
      link.setAttribute('href', url);
      link.setAttribute('download', `${filename}.csv`);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  }
}
