import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreateRouteRequest, CreateRouteResponse, Order, RouteModel, RoutesResponse } from '../../interfaces/route.interface';
import { OrdersResponse } from '../../interfaces/order.interface';

@Injectable({
  providedIn: 'root'
})
export class RouteService {
  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) { }

  getOrders(clientId?: string, sellerId?: string): Observable<OrdersResponse> {
    let url = `${this.apiUrl}/orders`;
    const params = [];

    if (clientId) params.push(`client_id=${clientId}`);
    if (sellerId) params.push(`seller_id=${sellerId}`);

    if (params.length > 0) {
      url += `?${params.join('&')}`;
    }

    return this.http.get<OrdersResponse>(url);
  }
  getOrderById(orderId: string): Observable<Order> {
  return this.http.get<Order>(`${this.apiUrl}/orders/${orderId}`);
}

  getRoutes(): Observable<RoutesResponse> {
    return this.http.get<RoutesResponse>(`${this.apiUrl}/routes`);
  }

  getRouteById(id: number): Observable<RouteModel> {
    return this.http.get<RouteModel>(`${this.apiUrl}/routes/${id}`);
  }

  createRoute(routeData: CreateRouteRequest): Observable<CreateRouteResponse> {
    return this.http.post<CreateRouteResponse>(`${this.apiUrl}/routes`, routeData);
  }
}
