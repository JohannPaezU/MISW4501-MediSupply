import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment.prod';
import { Vendedor, VendedorResponse, VendedorDetailResponse, CreateVendedorRequest, CreateVendedorResponse, ZonesResponse } from '../../interfaces/vendedor.interface';

@Injectable({
  providedIn: 'root'
})
export class VendedorService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }


  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Ocurrió un error al obtener la información.';

    if (error.status === 0) {
      errorMessage = 'No es posible conectar con el servidor. Por favor verifica tu conexión o inténtalo más tarde.';
    } else if (error.status >= 500) {
      errorMessage = 'El servidor presentó un problema. Intenta nuevamente más tarde.';
    } else if (error.status === 404) {
      errorMessage = 'El recurso solicitado no fue encontrado.';
    } else if (error.status === 401 || error.status === 403) {
      errorMessage = 'No tienes autorización para acceder a este recurso.';
    } else if (error.error instanceof ErrorEvent) {
      errorMessage = `Error del cliente: ${error.error.message}`;
    } else {
      errorMessage = `Error ${error.status}: ${error.message}`;
    }

    console.error('❌ Error capturado por handleError:', errorMessage, error);
    return throwError(() => new Error(errorMessage));
  }


  getVendedores(): Observable<Vendedor[]> {
    return this.http.get<VendedorResponse>(
      `${this.apiUrl}/sellers`,
      { headers: this.getAuthHeaders() }
    ).pipe(
      map(response => response.sellers),
      catchError(this.handleError)
    );
  }


  getVendedorById(id: number): Observable<Vendedor> {
    return this.http.get<VendedorDetailResponse>(
      `${this.apiUrl}/sellers/${id}`,
      { headers: this.getAuthHeaders() }
    ).pipe(
      catchError(this.handleError)
    );
  }

  createVendedor(vendedor: CreateVendedorRequest): Observable<CreateVendedorResponse> {
    return this.http.post<CreateVendedorResponse>(
      `${this.apiUrl}/sellers`,
      vendedor,
      { headers: this.getAuthHeaders() }
    ).pipe(
      catchError(this.handleError)
    );
  }

  getZones(): Observable<ZonesResponse> {
    return this.http.get<ZonesResponse>(
      `${this.apiUrl}/zones`,
      { headers: this.getAuthHeaders() }
    ).pipe(
      catchError(this.handleError)
    );
  }


  exportToCSV(vendedores: Vendedor[]): void {
    const headers = ['Nombre', 'Documento', 'Email', 'Teléfono', 'Zona', 'Fecha de Creación'];
    const csvData = vendedores.map(v => [
      v.fullname,
      v.document,
      v.email,
      v.phone,
      v.zone_description || '',
      v.created_at || ''
    ]);

    const csvContent = [
      headers.join(','),
      ...csvData.map(row => row.join(','))
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);

    link.setAttribute('href', url);
    link.setAttribute('download', `vendedores_${new Date().getTime()}.csv`);
    link.style.visibility = 'hidden';

    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
}
