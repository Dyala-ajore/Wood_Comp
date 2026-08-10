import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { ProductService } from '../../core/services/product.service';
import { ServiceApiService } from '../../core/services/service-api.service';
import { OrderService } from '../../core/services/order.service';
import { QuoteService } from '../../core/services/quote.service';

import { Product } from '../../core/models/product.model';
import { Service } from '../../core/models/service.model';
import { OrderResponse } from '../../core/models/order.model';
import { QuoteResponse } from '../../core/models/quote.model';

import { IlsCurrencyPipe } from '../../shared/pipes/ils-currency.pipe';
import { OrderStatusLabelPipe } from '../../shared/pipes/order-status-label.pipe';
import { QuoteStatusLabelPipe } from '../../shared/pipes/quote-status-label.pipe';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';

type AdminTab = 'products' | 'services' | 'orders' | 'quotes';

const ORDER_STATUSES = ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    IlsCurrencyPipe,
    OrderStatusLabelPipe,
    QuoteStatusLabelPipe,
    LoadingSpinnerComponent,
  ],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss',
})
export class AdminComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly productService = inject(ProductService);
  private readonly serviceApi = inject(ServiceApiService);
  private readonly orderService = inject(OrderService);
  private readonly quoteService = inject(QuoteService);

  readonly activeTab = signal<AdminTab>('products');
  readonly orderStatuses = ORDER_STATUSES;

  // ---- Products ----
  readonly products = signal<Product[]>([]);
  readonly productsLoading = signal(true);
  readonly showProductForm = signal(false);
  readonly productForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    productType: ['wood', Validators.required],
    price: [0, [Validators.required, Validators.min(0)]],
    stock: [0, [Validators.required, Validators.min(0)]],
    description: [''],
  });

  // ---- Services ----
  readonly services = signal<Service[]>([]);
  readonly servicesLoading = signal(true);
  readonly showServiceForm = signal(false);
  readonly serviceForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    price: [0, [Validators.required, Validators.min(0)]],
    description: [''],
  });

  // ---- Orders ----
  readonly orders = signal<OrderResponse[]>([]);
  readonly ordersLoading = signal(true);
  readonly orderStatusDrafts = signal<Record<number, string>>({});

  // ---- Quotes ----
  readonly quotes = signal<QuoteResponse[]>([]);
  readonly quotesLoading = signal(true);

  readonly actionError = signal<string | null>(null);

  ngOnInit(): void {
    this.loadProducts();
    this.loadServices();
    this.loadOrders();
    this.loadQuotes();
  }

  setTab(tab: AdminTab): void {
    this.activeTab.set(tab);
  }

  // ================= Products =================
  private loadProducts(): void {
    this.productsLoading.set(true);
    this.productService.getAll().subscribe({
      next: (list) => {
        this.products.set(list);
        this.productsLoading.set(false);
      },
      error: () => this.productsLoading.set(false),
    });
  }

  submitProduct(): void {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }
    this.actionError.set(null);
    this.productService.create(this.productForm.getRawValue()).subscribe({
      next: () => {
        this.productForm.reset({ name: '', productType: 'wood', price: 0, stock: 0, description: '' });
        this.showProductForm.set(false);
        this.loadProducts();
      },
      error: (err: HttpErrorResponse) => this.setActionError(err),
    });
  }

  deleteProduct(id: number): void {
    if (!confirm('متأكد إنك بدك تحذف هذا المنتج؟')) return;
    this.productService.delete(id).subscribe({
      next: () => this.loadProducts(),
      error: (err: HttpErrorResponse) => this.setActionError(err),
    });
  }

  // ================= Services =================
  private loadServices(): void {
    this.servicesLoading.set(true);
    this.serviceApi.getAll().subscribe({
      next: (list) => {
        this.services.set(list);
        this.servicesLoading.set(false);
      },
      error: () => this.servicesLoading.set(false),
    });
  }

  submitService(): void {
    if (this.serviceForm.invalid) {
      this.serviceForm.markAllAsTouched();
      return;
    }
    this.actionError.set(null);
    this.serviceApi.create(this.serviceForm.getRawValue()).subscribe({
      next: () => {
        this.serviceForm.reset({ name: '', price: 0, description: '' });
        this.showServiceForm.set(false);
        this.loadServices();
      },
      error: (err: HttpErrorResponse) => this.setActionError(err),
    });
  }

  deleteService(id: number): void {
    if (!confirm('متأكد إنك بدك تحذف هذه الخدمة؟')) return;
    this.serviceApi.delete(id).subscribe({
      next: () => this.loadServices(),
      error: (err: HttpErrorResponse) => this.setActionError(err),
    });
  }

  // ================= Orders =================
  private loadOrders(): void {
    this.ordersLoading.set(true);
    this.orderService.getAllOrders().subscribe({
      next: (list) => {
        this.orders.set([...list].sort((a, b) => b.id - a.id));
        this.ordersLoading.set(false);
      },
      error: () => this.ordersLoading.set(false),
    });
  }

  draftStatus(orderId: number, currentStatus: string): string {
    return this.orderStatusDrafts()[orderId] ?? currentStatus;
  }

  setDraftStatus(orderId: number, status: string): void {
    this.orderStatusDrafts.update((map) => ({ ...map, [orderId]: status }));
  }

  saveOrderStatus(order: OrderResponse): void {
    const newStatus = this.draftStatus(order.id, order.status);
    if (newStatus === order.status) return;
    this.actionError.set(null);
    this.orderService.updateStatus(order.id, newStatus).subscribe({
      next: () => this.loadOrders(),
      error: (err: HttpErrorResponse) => this.setActionError(err),
    });
  }

  // ================= Quotes =================
  private loadQuotes(): void {
    this.quotesLoading.set(true);
    this.quoteService.getAll().subscribe({
      next: (list) => {
        this.quotes.set([...list].sort((a, b) => b.id - a.id));
        this.quotesLoading.set(false);
      },
      error: () => this.quotesLoading.set(false),
    });
  }

  updateQuoteStatus(quote: QuoteResponse, status: string): void {
    this.actionError.set(null);
    this.quoteService.updateStatus(quote.id, { status }).subscribe({
      next: () => this.loadQuotes(),
      error: (err: HttpErrorResponse) => this.setActionError(err),
    });
  }

  private setActionError(err: HttpErrorResponse): void {
    this.actionError.set(
      (err.error as { message?: string } | undefined)?.message ?? 'حصل خطأ، حاول مجددًا.'
    );
  }
}
