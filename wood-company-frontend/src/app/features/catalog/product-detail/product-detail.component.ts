import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { ServiceApiService } from '../../../core/services/service-api.service';
import { CartService } from '../../../core/services/cart.service';
import { Product } from '../../../core/models/product.model';
import { Service } from '../../../core/models/service.model';
import { IlsCurrencyPipe } from '../../../shared/pipes/ils-currency.pipe';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    IlsCurrencyPipe,
    LoadingSpinnerComponent,
    EmptyStateComponent,
  ],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss',
})
export class ProductDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly productService = inject(ProductService);
  private readonly serviceApi = inject(ServiceApiService);
  readonly cart = inject(CartService);

  readonly product = signal<Product | null>(null);
  readonly services = signal<Service[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);

  readonly quantity = signal(1);
  readonly addedToCart = signal(false);

  readonly selectedServiceId = signal<number | null>(null);
  readonly serviceQuantity = signal(1);
  readonly serviceAdded = signal(false);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.productService.getById(id).subscribe({
      next: (product) => {
        this.product.set(product);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('المنتج غير موجود أو تعذّر تحميله.');
        this.loading.set(false);
      },
    });

    this.serviceApi.getAll().subscribe({
      next: (list) => this.services.set(list),
      error: () => {
        /* الخدمات اختيارية بهاي الصفحة - لا نوقف عرض المنتج لو فشلت */
      },
    });
  }

  increaseQty(): void {
    const p = this.product();
    if (!p) return;
    this.quantity.update((q) => Math.min(q + 1, p.stock));
  }

  decreaseQty(): void {
    this.quantity.update((q) => Math.max(1, q - 1));
  }

  addToCart(): void {
    const p = this.product();
    if (!p) return;
    this.cart.addItem(p, this.quantity());
    this.addedToCart.set(true);
    setTimeout(() => this.addedToCart.set(false), 1600);
  }

  addServiceToCart(): void {
    const p = this.product();
    const serviceId = this.selectedServiceId();
    if (!p || serviceId == null) return;

    const service = this.services().find((s) => s.id === serviceId);
    if (!service) return;

    this.cart.addServiceItem(p, service, this.serviceQuantity());
    this.serviceAdded.set(true);
    setTimeout(() => this.serviceAdded.set(false), 1600);
  }
}
