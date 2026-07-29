import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { IlsCurrencyPipe } from '../../shared/pipes/ils-currency.pipe';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterLink, IlsCurrencyPipe],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss',
})
export class CartComponent {
  readonly cart = inject(CartService);
  private readonly router = inject(Router);

  increase(productId: number, currentQty: number): void {
    this.cart.updateQuantity(productId, currentQty + 1);
  }

  decrease(productId: number, currentQty: number): void {
    this.cart.updateQuantity(productId, currentQty - 1);
  }

  remove(productId: number): void {
    this.cart.removeItem(productId);
  }

  increaseService(productId: number, serviceId: number, currentQty: number): void {
    this.cart.updateServiceItemQuantity(productId, serviceId, currentQty + 1);
  }

  decreaseService(productId: number, serviceId: number, currentQty: number): void {
    this.cart.updateServiceItemQuantity(productId, serviceId, currentQty - 1);
  }

  removeService(productId: number, serviceId: number): void {
    this.cart.removeServiceItem(productId, serviceId);
  }

  goToCheckout(): void {
    // لو المستخدم مش مسجّل دخول، authGuard رح يوجّهه للوج إن
    // وبعد النجاح يرجعه تلقائيًا هون بفضل returnUrl
    this.router.navigateByUrl('/checkout');
  }
}
