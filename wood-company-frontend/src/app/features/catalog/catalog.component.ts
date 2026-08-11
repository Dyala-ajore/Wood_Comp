import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductService } from '../../core/services/product.service';
import { CartService } from '../../core/services/cart.service';
import { Product } from '../../core/models/product.model';
import { IlsCurrencyPipe } from '../../shared/pipes/ils-currency.pipe';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, RouterLink, IlsCurrencyPipe],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.scss',
})
export class CatalogComponent implements OnInit {
  private readonly productService = inject(ProductService);
  readonly cart = inject(CartService);

  readonly products = signal<Product[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  /** لتتبع المنتج اللي تم إضافته لتوّه (لعرض تأكيد بصري مؤقت) */
  readonly justAdded = signal<number | null>(null);

  ngOnInit(): void {
    this.loadProducts();
  }

  addToCart(product: Product): void {
    this.cart.addItem(product, 1);
    this.justAdded.set(product.id);
    setTimeout(() => this.justAdded.set(null), 1400);
  }

  loadProducts(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.productService.getAll().subscribe({
      next: (list) => {
        this.products.set(list);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set(
            'تعذّر تحميل المنتجات، تأكد إن الخادم شغال وحاول مجددًا.'
        );
        this.loading.set(false);
      },
    });
  }
}
