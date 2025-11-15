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


export interface OrderInList {
  id: string;
  comments: string;
  delivery_date: string;
  status: string;
  created_at: string;
  distribution_center: DistributionCenter;
  route?: RouteInfo;
}

export interface RouteInfo {
  id: string;
  name: string;
  vehicle_plate: string;
  restrictions: string;
  delivery_deadline: string;
  created_at: string;
}

export interface DistributionCenter {
  id: string;
  name: string;
  address: string;
  city: string;
  country: string;
  created_at: string;
}

export interface OrdersListResponse {
  total_count: number;
  orders: OrderInList[];
}
