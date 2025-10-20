export interface ProviderBase {
  id: string;
  name: string;
  rit: string;
  city: string;
  country: string;
  image_url: string | null;
  email: string;
  phone: string;
  created_at: string;
}

export interface GetProvidersResponse {
  total_count: number;
  providers: ProviderBase[];
}

export interface ProviderCreateRequest {
  name: string;
  rit: string;
  city: string;
  country: string;
  image_url?: string | null;
  email: string;
  phone: string;
}

export interface ProviderCreateResponse extends ProviderBase {}

