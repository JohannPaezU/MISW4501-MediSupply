import { OrderInList } from "./order.interface";

export interface DistributionCenter {
  id: string;
  name: string;
  address: string;
  city: string;
  country: string;
  created_at: string;
}

export interface OrderInRoute {
  id: string;
  comments: string;
  delivery_date: string;
  status: string;
  created_at: string;
}

export interface RouteModel {
  id: string;
  name: string;
  vehicle_plate: string;
  restrictions: string;
  delivery_deadline: string;
  created_at: string;
  distribution_center: DistributionCenter;
  orders?: OrderInRoute[];
}

export interface RoutesResponse {
  total_count: number;
  routes: RouteModel[];
}

export interface CreateRouteRequest {
  name: string;
  vehicle_plate: string;
  restrictions: string;
  distribution_center_id: string;
  order_ids: string[];
}

export interface CreateRouteResponse {
  id: string;
  name: string;
  vehicle_plate: string;
  restrictions: string;
  delivery_deadline: string;
  created_at: string;
  distribution_center: DistributionCenter;
  orders: OrderInRoute[];
}

export interface RouteMapStop {
  order_id: string;
  order_status: string;
  delivery_date: string;
  client_name: string;
  client_address: string;
  client_phone: string;
  latitude: number;
  longitude: number;
}

export interface RouteMapResponse {
  id: string;
  name: string;
  vehicle_plate: string;
  restrictions: string;
  delivery_deadline: string;
  created_at: string;
  total_count: number;
  distribution_center: DistributionCenter;
  stops: RouteMapStop[];
}

export interface CreateRouteRequest {
  name: string;
  vehicle_plate: string;
  restrictions: string;
  distribution_center_id: string;
  order_ids: string[];
}

export interface CreateRouteResponse {
  id: string;
  name: string;
  vehicle_plate: string;
  restrictions: string;
  delivery_deadline: string;
  created_at: string;
  distribution_center: DistributionCenter;
  orders: OrderInRoute[];
}

export interface GroupedOrders {
  [distributionCenterId: string]: {
    centerName: string;
    centerInfo: string;
    orders: (OrderInList & { selected: boolean })[];
  };
}
