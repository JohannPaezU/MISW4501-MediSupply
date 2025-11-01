import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-item-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './item-dialog.html',
  styleUrls: ['./item-dialog.css']
})
export class ItemDialogComponent implements OnChanges {
  @Input() data: any;
  @Input() visible = false;
  @Input() title = 'Detalle';
  @Output() close = new EventEmitter<void>();

  displayData: { label: string; value: string }[] = [];

  private fieldTranslations: { [key: string]: string } = {
    // Campos comunes
    'id': 'ID',
    'name': 'Nombre',
    'created_at': 'Fecha de creación',
    'updated_at': 'Fecha de actualización',
    'city': 'Ciudad',
    'country': 'País',
    'phone': 'Teléfono',
    'email': 'Correo electrónico',
    'address': 'Dirección',
    ' Rit': 'RIT',


    'details': 'Detalles',
    'store': 'Tienda/Bodega',
    'batch': 'Lote',
    'image_url': 'URL Imagen',
    'due_date': 'Vencimiento',
    'stock': 'Stock',
    'price_per_unit': 'Precio por unidad',
    'price_per_unite': 'Precio por unidad',
    'provider_id': 'Proveedor ID',

    'period': 'Periodo',
    'goal': 'Meta (Unidades)',
    'product_name': 'Producto',
    'zone_description': 'Zona',
    'seller_name': 'Vendedor',

    'product.id': 'ID Producto',
    'product.name': 'Producto',
    'product.details': 'Detalles del Producto',
    'product.store': 'Tienda/Bodega',
    'product.batch': 'Lote',
    'product.stock': 'Stock',
    'product.price_per_unit': 'Precio',

    'zone.id': 'ID Zona',
    'zone.description': 'Zona',
    'zone.name': 'Nombre de Zona',

    'seller.id': 'ID Vendedor',
    'seller.full_name': 'Vendedor',
    'seller.name': 'Nombre',
    'seller.email': 'Email',
    'seller.phone': 'Teléfono',

    'provider.id': 'ID Proveedor',
    'provider.name': 'Proveedor',
    'provider.email': 'Email Proveedor',
    'provider.phone': 'Teléfono Proveedor',
  };

  ngOnChanges(changes: SimpleChanges) {
    if (changes['data'] && this.data) {
      this.displayData = this.flattenObject(this.data);
    }
  }

  onClose() {
    this.close.emit();
  }

  private flattenObject(obj: any, parentKey: string = ''): { label: string; value: string }[] {
    let result: { label: string; value: string }[] = [];

    for (const key in obj) {
      if (!obj.hasOwnProperty(key)) continue;
      const newKey = parentKey ? `${parentKey}.${key}` : key;
      const value = obj[key];

      if (value && typeof value === 'object' && !Array.isArray(value)) {
        result = result.concat(this.flattenObject(value, newKey));
      } else {
        result.push({
          label: this.translateField(newKey),
          value: this.formatValue(value, key)
        });
      }
    }

    return result;
  }

  private translateField(key: string): string {
    if (this.fieldTranslations[key]) {
      return this.fieldTranslations[key];
    }

    const lastPart = key.split('.').pop() || key;
    if (this.fieldTranslations[lastPart]) {
      return this.fieldTranslations[lastPart];
    }

    return this.formatLabel(lastPart);
  }

  private formatValue(value: any, key: string): string {
    if (value === null || value === undefined) {
      return 'N/A';
    }

    if (key.includes('date') || key.includes('created_at') || key.includes('updated_at')) {
      const date = new Date(value);
      if (!isNaN(date.getTime())) {
        return date.toLocaleDateString('es-CO', {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric',
          hour: '2-digit',
          minute: '2-digit'
        });
      }
    }

    if (key.includes('price') || key.includes('precio')) {
      const num = Number(value);
      if (!isNaN(num)) {
        return new Intl.NumberFormat('es-CO', {
          style: 'currency',
          currency: 'COP'
        }).format(num);
      }
    }

    if (key === 'stock') {
      const num = Number(value);
      if (!isNaN(num)) {
        return new Intl.NumberFormat('es-CO').format(num);
      }
    }

    return String(value);
  }

  private formatLabel(key: string): string {
    return key
      .replace(/_/g, ' ')
      .replace(/\b\w/g, (c) => c.toUpperCase());
  }
}
