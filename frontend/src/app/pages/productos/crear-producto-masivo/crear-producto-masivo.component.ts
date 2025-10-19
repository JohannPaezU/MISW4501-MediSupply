import { Component, ViewChild, ElementRef, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ReporteResultado } from '../../../interfaces/reporteResultado.interface';
import { ProductService } from '../../../services/productos/product.service';
import { ProductCreateRequest, ProductRow } from '../../../interfaces/producto.interface';
import * as XLSX from 'xlsx';
import * as Papa from 'papaparse';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-crear-productos-masivo',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './crear-producto-masivo.component.html',
  styleUrls: ['./crear-producto-masivo.component.css']
})
export class CrearProductoMasivoComponent {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  archivoSeleccionado: File | null = null;
  nombreArchivo: string = '';
  mostrarReporte: boolean = false;
  isProcessing: boolean = false;
  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  reporte: ReporteResultado = {
    totalRegistros: 0,
    registrosExitosos: 0,
    cantidadErrores: 0,
    errores: []
  };

  constructor(
    private productService: ProductService,
    private cdr: ChangeDetectorRef
  ) { }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.processFile(input.files[0]);
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();

    if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
      const file = event.dataTransfer.files[0];
      if (this.isValidFileType(file)) {
        this.processFile(file);
      } else {
        this.showToast('Tipo de archivo no válido. Solo se aceptan CSV, XLSX o XLS', 'error');
      }
    }
  }

  processFile(file: File): void {
    this.archivoSeleccionado = file;
    this.nombreArchivo = file.name;
    this.mostrarReporte = false;
  }

  isValidFileType(file: File): boolean {
    const validTypes = [
      'text/csv',
      'application/vnd.ms-excel',
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    ];
    return validTypes.includes(file.type) ||
      file.name.endsWith('.csv') ||
      file.name.endsWith('.xlsx') ||
      file.name.endsWith('.xls');
  }

  triggerFileInput(): void {
    this.fileInput.nativeElement.click();
  }

  removeFile(event: Event): void {
    event.stopPropagation();
    this.archivoSeleccionado = null;
    this.nombreArchivo = '';
    this.mostrarReporte = false;
    if (this.fileInput) {
      this.fileInput.nativeElement.value = '';
    }
  }

  async crearProductos(): Promise<void> {
    if (!this.archivoSeleccionado) {
      this.showToast('Por favor selecciona un archivo', 'error');
      return;
    }

    this.isProcessing = true;
    this.mostrarReporte = false;
    this.cdr.detectChanges();

    try {
      const productos = await this.parseFile(this.archivoSeleccionado);

      if (productos.length === 0) {
        this.showToast('El archivo no contiene productos válidos', 'error');
        this.isProcessing = false;
        return;
      }

      const validationResult = this.validateProducts(productos);

      if (validationResult.errors.length > 0) {
        this.reporte = {
          totalRegistros: productos.length,
          registrosExitosos: 0,
          cantidadErrores: validationResult.errors.length,
          errores: validationResult.errors
        };
        this.mostrarReporte = true;
        this.isProcessing = false;
        this.showToast('Hay errores de validación en el archivo', 'error');
        this.cdr.detectChanges();
        return;
      }

      this.uploadProducts(validationResult.validProducts);

    } catch (error) {
      console.error('Error al procesar el archivo:', error);
      this.showToast('Error al procesar el archivo', 'error');
      this.isProcessing = false;
      this.cdr.detectChanges();
    }
  }

  private parseFile(file: File): Promise<ProductRow[]> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = (e: any) => {
        try {
          if (file.name.endsWith('.csv')) {
            this.parseCSV(e.target.result, resolve, reject);
          } else {
            this.parseExcel(e.target.result, resolve, reject);
          }
        } catch (error) {
          reject(error);
        }
      };

      reader.onerror = () => reject(new Error('Error al leer el archivo'));

      if (file.name.endsWith('.csv')) {
        reader.readAsText(file);
      } else {
        reader.readAsArrayBuffer(file);
      }
    });
  }

  private parseCSV(content: string, resolve: Function, reject: Function): void {
    Papa.parse(content, {
      header: true,
      skipEmptyLines: true,
      complete: (results) => {
        resolve(results.data as ProductRow[]);
      },
      error: (error: Error) => {
        reject(error);
      }
    });
  }

  private parseExcel(content: ArrayBuffer, resolve: Function, reject: Function): void {
    try {
      const workbook = XLSX.read(content, { type: 'array' });
      const firstSheet = workbook.Sheets[workbook.SheetNames[0]];
      const data = XLSX.utils.sheet_to_json(firstSheet) as ProductRow[];
      resolve(data);
    } catch (error) {
      reject(error);
    }
  }

  private validateProducts(productos: ProductRow[]): { validProducts: ProductCreateRequest[], errors: string[] } {
    const validProducts: ProductCreateRequest[] = [];
    const errors: string[] = [];

    productos.forEach((producto, index) => {
      const rowNum = index + 2;
      const rowErrors: string[] = [];

      // Validar name (3-100 caracteres)
      if (!producto.name || producto.name.trim().length < 3 || producto.name.trim().length > 100) {
        rowErrors.push('Nombre debe tener entre 3 y 100 caracteres');
      }

      // Validar details (10-500 caracteres)
      if (!producto.details || producto.details.trim().length < 10 || producto.details.trim().length > 500) {
        rowErrors.push('Detalles deben tener entre 10 y 500 caracteres');
      }

      // Validar store (3-100 caracteres)
      if (!producto.store || producto.store.trim().length < 3 || producto.store.trim().length > 100) {
        rowErrors.push('Bodega debe tener entre 3 y 100 caracteres');
      }

      // Validar batch (5-50 caracteres)
      if (!producto.batch || producto.batch.trim().length < 5 || producto.batch.trim().length > 50) {
        rowErrors.push('Lote debe tener entre 5 y 50 caracteres');
      }

      // Validar image_url (opcional, 10-300 caracteres)
      if (producto.image_url && producto.image_url.trim().length > 0) {
        if (producto.image_url.trim().length < 10 || producto.image_url.trim().length > 300) {
          rowErrors.push('URL de imagen debe tener entre 10 y 300 caracteres');
        }
      }

      // Validar due_date (formato YYYY-MM-DD)
      if (!producto.due_date || !this.isValidDate(producto.due_date)) {
        rowErrors.push('Fecha de vencimiento debe estar en formato YYYY-MM-DD');
      }

      // Validar stock (mayor a 0)
      const stock = Number(producto.stock);
      if (isNaN(stock) || stock <= 0) {
        rowErrors.push('Stock debe ser un número mayor a 0');
      }

      // Validar price_per_unite (mayor a 0)
      const price = Number(producto.price_per_unite);
      if (isNaN(price) || price <= 0) {
        rowErrors.push('Precio por unidad debe ser un número mayor a 0');
      }

      // Validar provider_id (36 caracteres)
      if (!producto.provider_id || producto.provider_id.trim().length !== 36) {
        rowErrors.push('ID de proveedor debe tener exactamente 36 caracteres');
      }

      if (rowErrors.length > 0) {
        errors.push(`Fila ${rowNum}: ${rowErrors.join(', ')}`);
      } else {
        validProducts.push({
          name: producto.name.trim(),
          details: producto.details.trim(),
          store: producto.store.trim(),
          batch: producto.batch.trim(),
          image_url: producto.image_url?.trim() || null,
          due_date: producto.due_date.trim(),
          stock: Number(producto.stock),
          price_per_unite: Number(producto.price_per_unite),
          provider_id: producto.provider_id.trim()
        });
      }
    });

    return { validProducts, errors };
  }

  private isValidDate(dateString: string): boolean {
    // Formato YYYY-MM-DD
    const regex = /^\d{4}-\d{2}-\d{2}$/;
    if (!regex.test(dateString)) return false;

    const date = new Date(dateString);
    return date instanceof Date && !isNaN(date.getTime());
  }

  private uploadProducts(productos: ProductCreateRequest[]): void {
    this.productService.createProductsBulk({ products: productos })
      .pipe(
        finalize(() => {
          this.isProcessing = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (response) => {
          this.reporte = {
            totalRegistros: response.rows_total,
            registrosExitosos: response.rows_inserted,
            cantidadErrores: response.errors,
            errores: response.errors_details
          };
          this.mostrarReporte = true;

          if (response.success) {
            this.showToast('Productos creados exitosamente', 'success');
          } else {
            this.showToast('Algunos productos no pudieron ser creados', 'error');
          }

          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Error al crear productos:', error);
          this.showToast('Error al crear los productos', 'error');
          this.cdr.detectChanges();
        }
      });
  }

  showToast(message: string, type: 'success' | 'error'): void {
    this.toastMessage = message;
    this.toastType = type;
    this.cdr.detectChanges();

    setTimeout(() => {
      this.toastMessage = null;
      this.cdr.detectChanges();
    }, 4000);
  }

  descargarPlantilla(): void {
    const headers = [
      'name',
      'details',
      'store',
      'batch',
      'image_url',
      'due_date',
      'stock',
      'price_per_unite',
      'provider_id'
    ];

    const ejemploFila = [
      'Producto Ejemplo',
      'Descripción detallada del producto con al menos 10 caracteres para cumplir validación',
      'Bodega Central',
      'LOTE-2024-001',
      'https://ejemplo.com/imagen-producto.jpg',
      '2025-12-31',
      100,
      25.50,
      '123e4567-e89b-12d3-a456-426614174000'
    ];

    const segundoEjemplo = [
      'Arroz Premium 1kg',
      'Arroz de grano largo premium, ideal para todo tipo de preparaciones culinarias',
      'Bodega Norte',
      'LOTE-2024-ARZ-001',
      'https://ejemplo.com/arroz-premium.jpg',
      '2026-06-15',
      500,
      12.99,
      '123e4567-e89b-12d3-a456-426614174000'
    ];

    const ws = XLSX.utils.aoa_to_sheet([headers, ejemploFila, segundoEjemplo]);

    ws['!cols'] = [
      { wch: 20 },
      { wch: 50 },
      { wch: 20 },
      { wch: 20 },
      { wch: 35 },
      { wch: 15 },
      { wch: 10 },
      { wch: 15 },
      { wch: 38 }
    ];

    const headerStyle = {
      font: { bold: true, color: { rgb: "FFFFFF" } },
      fill: { fgColor: { rgb: "1976D2" } },
      alignment: { horizontal: "center", vertical: "center" }
    };

    headers.forEach((_, colIndex) => {
      const cellAddress = XLSX.utils.encode_cell({ r: 0, c: colIndex });
      if (!ws[cellAddress]) ws[cellAddress] = { t: 's', v: '' };
      ws[cellAddress].s = headerStyle;
    });

    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Productos');

    XLSX.writeFile(wb, 'plantilla_productos.xlsx');

    this.showToast('Plantilla Excel descargada exitosamente', 'success');
  }
}
