export interface CartItem {
  productId: number;
  name: string;
  price: number;
  quantity: number;
  stock: number;
  imageUrl?: string;
}

/** خدمة (قص/كبس...) مرتبطة بمنتج معيّن داخل السلة */
export interface CartServiceItem {
  productId: number;
  productName: string;
  serviceId: number;
  serviceName: string;
  price: number;
  quantity: number;
}
