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
        return response.orders.map((order: any) => ({
          id: order.id,
          vendedor: order.seller?.full_name || 'Sin vendedor',
          vendedor_id: order.seller?.id || '',
          producto: order.product?.name || 'Sin producto',
          producto_id: order.product?.id || '',
          zona: order.seller?.zone?.description || 'Sin zona',
          zona_id: order.seller?.zone?.id || '',
          ventas: order.total || 0,
          meta: order.meta || 0,
          porcentajeMeta: order.meta ? Math.round((order.total / order.meta) * 100) : 0,
          periodo: order.delivery_date ? new Date(order.delivery_date).toISOString().slice(0, 10) : '',
        }));
      })
    );
  }
}
