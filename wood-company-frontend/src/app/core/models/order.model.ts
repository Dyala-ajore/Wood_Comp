export interface OrderItemRequest {
  productId: number;
  quantity: number;
}

export interface OrderServiceItemRequest {
  productId: number;
  serviceId: number;
  quantity: number;
}

export interface CreateOrderRequest {
  items: OrderItemRequest[];
  serviceItems?: OrderServiceItemRequest[];
}

export interface OrderItemView {
  productId: number;
  productName: string;
  quantity: number;
  price: number;
}

export interface OrderServiceItemView {
  serviceId: number;
  serviceName: string;
  productId: number;
  productName: string;
  quantity: number;
  price: number;
}

export interface OrderResponse {
  id: number;
  userId: number;
  status: string;
  totalPrice: number;
  createdAt: string;
  items: OrderItemView[];
  serviceItems: OrderServiceItemView[];
}
