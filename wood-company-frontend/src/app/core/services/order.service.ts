import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreateOrderRequest, OrderResponse } from '../models/order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly http = inject(HttpClient);

  createOrder(payload: CreateOrderRequest): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(`${environment.apiUrl}/orders`, payload);
  }

  getMyOrders(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${environment.apiUrl}/orders/my`);
  }
}
