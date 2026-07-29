import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrderService } from '../../core/services/order.service';
import { OrderResponse } from '../../core/models/order.model';
import { IlsCurrencyPipe } from '../../shared/pipes/ils-currency.pipe';
import { OrderStatusLabelPipe } from '../../shared/pipes/order-status-label.pipe';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    IlsCurrencyPipe,
    OrderStatusLabelPipe,
    LoadingSpinnerComponent,
    EmptyStateComponent,
  ],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.scss',
})
export class OrdersComponent implements OnInit {
  private readonly orderService = inject(OrderService);

  readonly orders = signal<OrderResponse[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  /** رقم الطلب المفتوح حاليًا بالتفاصيل (null = كلهم مطويين) */
  readonly expandedOrderId = signal<number | null>(null);

  ngOnInit(): void {
    this.orderService.getMyOrders().subscribe({
      next: (list) => {
        // الأحدث أول
        this.orders.set([...list].sort((a, b) => b.id - a.id));
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('تعذّر تحميل طلباتك، حاول مجددًا لاحقًا.');
        this.loading.set(false);
      },
    });
  }

  toggleExpand(orderId: number): void {
    this.expandedOrderId.update((current) => (current === orderId ? null : orderId));
  }

  statusClass(status: string): string {
    return `status-badge status-badge--${status.toLowerCase()}`;
  }
}
