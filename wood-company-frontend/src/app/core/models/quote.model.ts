export interface CreateQuoteRequest {
  productId: number;
  quantity: number;
  message?: string;
}

export interface UpdateQuoteStatusRequest {
  status: string;
}

export interface QuoteResponse {
  id: number;
  userId: number;
  productId: number;
  productName: string;
  quantity: number;
  message?: string;
  status: string;
  createdAt: string;
}
