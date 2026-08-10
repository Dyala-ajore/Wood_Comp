export interface Service {
  id: number;
  name: string;
  description?: string;
  price: number;
}

export interface CreateServiceRequest {
  name: string;
  description?: string;
  price: number;
}

export type UpdateServiceRequest = Partial<CreateServiceRequest>;
