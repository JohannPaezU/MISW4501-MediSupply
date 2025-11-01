import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { catchError, finalize, of } from 'rxjs';
import { ProductService } from '../../../services/productos/product.service';
import { ProductCreateResponse } from '../../../interfaces/producto.interface';
import { CsvExportService } from '../../../services/utilities/csv.service';
import { LoadingSpinnerComponent } from '../../../components/loading-spinner/loading-spinner.component';
import { ItemDialogComponent } from '../../../components/item-dialog/item-dialog';

@Component({
  selector: 'app-lista-productos',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule, LoadingSpinnerComponent, ItemDialogComponent],
  templateUrl: './lista-productos.component.html',
  styleUrls: ['./lista-productos.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListaProductosComponent {
  productos: ProductCreateResponse[] = [];
  isLoading = false;
  hasError = false;
  copiedCell: string | null = null;
  dialogVisible = false;
  selectedItem: any = null;


  pageSize = 10;
  currentPage = 1;
  totalItems = 0;

  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  constructor(
    private router: Router,
    private productService: ProductService,
    private csvExportService: CsvExportService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.cargarProductos();
  }

  cargarProductos(): void {
    this.isLoading = true;
    this.hasError = false;
    this.cdr.detectChanges();

    this.productService
      .getProducts(this.currentPage, this.pageSize)
      .pipe(
        catchError((error) => {
          console.error('Error al cargar productos:', error);
          this.hasError = true;
          this.showToast('Error al cargar los productos.', 'error');
          return of({ products: [], total_count: 0 });
        }),
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe((data) => {
        this.productos = data.products;
        this.totalItems = data.total_count;
        this.cdr.detectChanges();
      });
  }

  mostrarDetalle(item: any) {
    this.selectedItem = item;
    this.dialogVisible = true;
    this.cdr.detectChanges();
  }

  copyToClipboard(id: string, type: 'product' | 'provider', productId: string): void {
    navigator.clipboard.writeText(id).then(() => {
      this.copiedCell = `${type}-${productId}`;
      this.cdr.detectChanges();
      setTimeout(() => {
        this.copiedCell = null;
        this.cdr.detectChanges();
      }, 1500);
    }).catch(err => console.error('Error al copiar ID: ', err));
  }

  /** ✅ Usa el servicio CsvExportService */
  exportarCSV(): void {
    if (!this.productos || this.productos.length === 0) {
      this.showToast('No hay productos para exportar.', 'error');
      return;
    }

    const headers = {
      id: 'ID',
      name: 'Nombre',
      details: 'Detalles',
      store: 'Tienda/Bodega',
      batch: 'Lote',
      image_url: 'URL Imagen',
      due_date: 'Vencimiento',
      stock: 'Stock',
      price_per_unite: 'Precio por unidad',
      provider_id: 'Proveedor ID',
      created_at: 'Fecha creación'
    };

    this.csvExportService.exportarACsv(this.productos, 'productos', headers);
    this.showToast('Productos exportados correctamente.', 'success');
  }

  crearProducto(): void {
    this.router.navigate(['/productos/crear']);
  }

  verDetalle(producto: ProductCreateResponse): void {
    this.router.navigate(['/productos/detalle', producto.id]);
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.pageSize = Number(target.value);
    this.currentPage = 1;
    this.cargarProductos();
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.cargarProductos();
    }
  }

  nextPage(): void {
    const totalPages = Math.ceil(this.totalItems / this.pageSize);
    if (this.currentPage < totalPages) {
      this.currentPage++;
      this.cargarProductos();
    }
  }

  get startItem(): number {
    return this.productos.length > 0 ? (this.currentPage - 1) * this.pageSize + 1 : 0;
  }

  get endItem(): number {
    return Math.min(this.currentPage * this.pageSize, this.totalItems);
  }

  retry(): void {
    this.cargarProductos();
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
}
