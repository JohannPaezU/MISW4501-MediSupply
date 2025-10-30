import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ProductCreateRequest,
  ProductCreateResponse,
  ProductCreateBulkRequest,
  ProductCreateBulkResponse
} from '../../interfaces/producto.interface';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = `${environment.apiUrl}/products`;

  constructor(private http: HttpClient) { }

  createProduct(productData: ProductCreateRequest): Observable<ProductCreateResponse> {
    return this.http.post<ProductCreateResponse>(this.apiUrl, productData);
  }

  createProductsBulk(bulkData: ProductCreateBulkRequest): Observable<ProductCreateBulkResponse> {
    return this.http.post<ProductCreateBulkResponse>(`${this.apiUrl}-batch`, bulkData);
  }

  getProducts(page?: number, limit?: number): Observable<{ total_count: number; products: ProductCreateResponse[] }> {
    if (page && limit) {
      return this.http.get<{ total_count: number; products: ProductCreateResponse[] }>(
        `${this.apiUrl}?page=${page}&limit=${limit}`
      );
    }
    return this.http.get<{ total_count: number; products: ProductCreateResponse[] }>(this.apiUrl);
  }

  getAllProducts(): Observable<{ total_count: number; products: ProductCreateResponse[] }> {
    return this.http.get<{ total_count: number; products: ProductCreateResponse[] }>(this.apiUrl);
  }
}
