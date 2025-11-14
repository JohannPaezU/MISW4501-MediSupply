export interface Order {
  id: string;
  comments: string;
  delivery_date: string;
  status: string;
  created_at: string;
  seller: Seller;
  client: Client;
  distribution_center: DistributionCenter;
  products: Product[];
}

export interface Seller {
  id: string;
  full_name: string;
  email: string;
  phone: string;
  doi: string;
  role: string;
  created_at: string;
}

export interface Client {
  id: string;
  full_name: string;
  email: string;
  phone: string;
  doi: string;
  address: string;
  role: string;
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

export interface Product {
  id: string;
  name: string;
  store: string;
  batch: string;
  due_date: string;
  price_per_unit: number;
  quantity: number;
  image_url: string;
}

export interface OrdersResponse {
  total_count: number;
  orders: { id: string; status: string }[];
}

export interface RouteModel {
  id: number;
  origin: string;
  origin_lat: number;
  origin_long: number;
  destiny: string;
  destiny_lat: number;
  destiny_long: number;
  date_limit: string;
  restrictions: string;
  vehicle: string;
  created_at: string;
}

export interface RoutesResponse {
  routes: RouteModel[];
}

export interface CreateRouteRequest {
  products: string[];
  origin: string;
  origin_lat: number;
  origin_long: number;
  destiny: string;
  destiny_lat: number;
  destiny_long: number;
  date_limit: string;
  restrictions: string;
  vehicle: string;
}

export interface CreateRouteResponse {
  id: number;
  origin: string;
  origin_lat: number;
  origin_long: number;
  destiny: string;
  destiny_lat: number;
  destiny_long: number;
  date_limit: string;
  restrictions: string;
  vehicle: string;
  products: number[];
  created_at: string;
}
