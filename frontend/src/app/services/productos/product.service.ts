import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ProductCreateRequest, ProductCreateResponse } from '../../interfaces/producto.interface';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = `${environment.apiUrl}/products`;

  constructor(private http: HttpClient) { }

  /**
   * Envía una solicitud para crear un nuevo producto.
   * @param productData Los datos del producto a crear.
   * @returns Un Observable con la respuesta del servidor.
   */
  createProduct(productData: ProductCreateRequest): Observable<ProductCreateResponse> {
    return this.http.post<ProductCreateResponse>(this.apiUrl, productData);
  }
}
