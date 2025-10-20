import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreatePlanVentaRequest, CreatePlanVentaResponse, GetPlanesVentaResponse, GetPlanVentaResponse } from '../../interfaces/planVenta.interface';

@Injectable({
  providedIn: 'root'
})
export class PlanVentaService {
  private apiUrl = `${environment.apiUrl}/selling-plans`;

  constructor(private http: HttpClient) { }

  createPlanVenta(planData: CreatePlanVentaRequest): Observable<CreatePlanVentaResponse> {
    return this.http.post<CreatePlanVentaResponse>(this.apiUrl, planData);
  }

  getPlanesVenta(): Observable<GetPlanesVentaResponse> {
    return this.http.get<GetPlanesVentaResponse>(this.apiUrl);
  }

  getPlanVentaById(id: string): Observable<GetPlanVentaResponse> {
    return this.http.get<GetPlanVentaResponse>(`${this.apiUrl}/${id}`);
  }
}
