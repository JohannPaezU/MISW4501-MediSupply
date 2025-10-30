import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

import {
  Vendedor,
  VendedorResponse,
  Zone,
  ZonesResponse,
  CreateVendedorRequest,
  CreateVendedorResponse
} from '../../interfaces/vendedor.interface';

@Injectable({
  providedIn: 'root'
})
export class VendedorService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  getZonas(): Observable<Zone[]> {
    return this.http.get<ZonesResponse>(`${this.apiUrl}/zones`).pipe(
      map(response => response.zones)
    );
  }
  createVendedor(vendedorData: CreateVendedorRequest): Observable<CreateVendedorResponse> {
    return this.http.post<CreateVendedorResponse>(`${this.apiUrl}/sellers`, vendedorData);
  }

  getVendedores(): Observable<VendedorResponse> {
    return this.http.get<VendedorResponse>(`${this.apiUrl}/sellers`);
  }

  getVendedorById(id: string): Observable<Vendedor> {
    return this.http.get<Vendedor>(`${this.apiUrl}/sellers/${id}`);
  }
}
