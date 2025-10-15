import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment'; // Es mejor no usar .prod aquí directamente

// ¡Esta es la línea que se corrigió!
import {
  Zone,
  ZonesResponse,
  CreateVendedorRequest,
  CreateVendedorResponse
} from '../../interfaces/vendedor.interface';

@Injectable({
  providedIn: 'root'
})
export class CrearVendedorService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  /**
   * Obtiene la lista de zonas desde el backend.
   */
  getZonas(): Observable<Zone[]> {
    return this.http.get<ZonesResponse>(`${this.apiUrl}/zones`).pipe(
      // Ahora el tipo 'response.zones' (Zone[]) coincide perfectamente
      map(response => response.zones)
    );
  }

  /**
   * Envía la solicitud para crear un nuevo vendedor.
   * @param vendedorData Los datos del vendedor a crear.
   */
  createVendedor(vendedorData: CreateVendedorRequest): Observable<CreateVendedorResponse> {
    return this.http.post<CreateVendedorResponse>(`${this.apiUrl}/sellers`, vendedorData);
  }
}
