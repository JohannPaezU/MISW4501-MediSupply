import { ProductCreateResponse } from './producto.interface';
import { Vendedor, Zone } from './vendedor.interface';

export interface PlanVenta {
  id?: string;
  period: string;
  goal: number;
  created_at?: string;
  product?: ProductCreateResponse;
  zone?: Zone;
  seller?: Vendedor;
}

export interface CreatePlanVentaRequest {
  period: string;
  goal: number;
  product_id: string;
  zone_id: string;
  seller_id: string;
}

export interface CreatePlanVentaResponse extends PlanVenta {}

export interface GetPlanVentaResponse extends PlanVenta {}

export interface GetPlanesVentaResponse {
  total_count: number;
  selling_plans: PlanVenta[];
}
