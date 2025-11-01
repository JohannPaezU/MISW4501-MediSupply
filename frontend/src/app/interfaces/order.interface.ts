import { Vendedor, ZonaGeografica } from './vendedor.interface';

export interface OrderProduct {
  id: string;
  name: string;
  store: string;
  batch: string;
  due_date: string;
  price_per_unit: number;
  quantity: number;
}

export interface Order {
  id: string;
  delivery_date: string;
  status: string;
  created_at: string;
  seller: Vendedor;
  products: OrderProduct[];
}

export interface OrdersResponse {
  total_count: number;
  orders: Order[];
}
