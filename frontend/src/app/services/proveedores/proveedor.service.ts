import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { GetProvidersResponse, ProviderCreateRequest, ProviderCreateResponse } from '../../interfaces/proveedor.intrface';

@Injectable({
  providedIn: 'root'
})
export class ProveedorService {
  private apiUrl = `${environment.apiUrl}/providers`;

  constructor(private http: HttpClient) { }

  createProvider(provider: ProviderCreateRequest): Observable<ProviderCreateResponse> {
    return this.http.post<ProviderCreateResponse>(this.apiUrl, provider);
  }

  getProviders(): Observable<GetProvidersResponse> {
    return this.http.get<GetProvidersResponse>(this.apiUrl);
  }
}

