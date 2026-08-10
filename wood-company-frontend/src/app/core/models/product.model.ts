export interface ProductImage {
  id: number;
  productId: number;
  imageUrl: string;
}

export interface Product {
  id: number;
  name: string;
  description?: string;
  price: number;
  stock: number;
  length?: number;
  width?: number;
  thickness?: number;
  productType: string;
  createdAt: string;
  images: ProductImage[];
}

export interface CreateProductRequest {
  name: string;
  description?: string;
  price: number;
  stock: number;
  length?: number;
  width?: number;
  thickness?: number;
  productType: string;
}

export type UpdateProductRequest = Partial<CreateProductRequest>;
