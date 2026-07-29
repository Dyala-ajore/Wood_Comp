import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { CartService } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';
import { IlsCurrencyPipe } from '../../shared/pipes/ils-currency.pipe';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, RouterLink, IlsCurrencyPipe],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.scss',
})
export class CheckoutComponent {
  readonly cart = inject(CartService);
  private readonly orderService = inject(OrderService);

  readonly submitting = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successOrderId = signal<number | null>(null);

  confirmOrder(): void {
    if (this.cart.isEmpty() || this.submitting()) return;

    this.submitting.set(true);
    this.errorMessage.set(null);

    const payload = {
      items: this.cart.items().map((i) => ({ productId: i.productId, quantity: i.quantity })),
      serviceItems: this.cart.serviceItems().map((i) => ({
        productId: i.productId,
        serviceId: i.serviceId,
        quantity: i.quantity,
      })),
    };

    this.orderService.createOrder(payload).subscribe({
      next: (order) => {
        this.submitting.set(false);
        this.successOrderId.set(order.id);
        this.cart.clear();
      },
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);
        this.errorMessage.set(
          (err.error as { message?: string } | undefined)?.message ??
            'تعذّر إتمام الطلب، حاول مجددًا.'
        );
      },
    });
  }
}
