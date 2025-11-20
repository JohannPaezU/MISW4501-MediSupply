import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CreateRouteRequest,
  CreateRouteResponse,
  RouteModel,
  RoutesResponse,
  RouteMapResponse
} from '../../interfaces/route.interface';
import { OrdersListResponse } from '../../interfaces/order.interface';

@Injectable({
  providedIn: 'root'
})
export class RouteService {
  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) { }

  getAllOrders(): Observable<OrdersListResponse> {
    return this.http.get<OrdersListResponse>(`${this.apiUrl}/orders`);
  }

  getRoutes(): Observable<RoutesResponse> {
    return this.http.get<RoutesResponse>(`${this.apiUrl}/routes`);
  }

  getRouteById(id: string): Observable<RouteModel> {
    return this.http.get<RouteModel>(`${this.apiUrl}/routes/${id}`);
  }

  getRouteMap(id: string): Observable<RouteMapResponse> {
    return this.http.get<RouteMapResponse>(`${this.apiUrl}/routes/${id}/map`);
  }

  createRoute(routeData: CreateRouteRequest): Observable<CreateRouteResponse> {
    return this.http.post<CreateRouteResponse>(`${this.apiUrl}/routes`, routeData);
  }
}
