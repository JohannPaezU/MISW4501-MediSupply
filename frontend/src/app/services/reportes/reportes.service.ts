import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ReporteData } from '../../interfaces/reporte.interface';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private apiUrl = `${environment.apiUrl}/reports/orders`;

  constructor(private http: HttpClient) {}

  getReportes(): Observable<ReporteData[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map(response => {
        const reportes: ReporteData[] = [];

        // Iterar sobre cada orden
        response.orders.forEach((order: any) => {
          // Iterar sobre cada producto dentro de la orden
          order.products.forEach((product: any) => {
            reportes.push({
              id: `${order.id}_${product.id}`, // ID único combinando orden + producto
              vendedor: order.seller?.full_name || 'Sin vendedor',
              vendedor_id: order.seller?.id || '',
              producto: product.name || 'Sin producto',
              producto_id: product.id || '',
              zona: order.seller?.zone?.description || 'Sin zona',
              zona_id: order.seller?.zone?.id || '',
              ventas: (product.price_per_unit || 0) * (product.quantity || 0), // Calcular ventas por producto
              meta: 0, // Se establecerá desde el plan de ventas
              porcentajeMeta: 0, // Se calculará después
              periodo: order.delivery_date ? new Date(order.delivery_date).toISOString().slice(0, 10) : '',
              // Datos adicionales que podrían ser útiles
              cantidad: product.quantity || 0,
              precio_unitario: product.price_per_unit || 0,
              estado_orden: order.status || '',
              comentarios: order.comments || ''
            });
          });
        });

        return reportes;
      })
    );
  }
}
