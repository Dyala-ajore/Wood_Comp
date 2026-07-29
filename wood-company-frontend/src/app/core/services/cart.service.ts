import { Injectable, computed, effect, signal } from '@angular/core';
import { CartItem, CartServiceItem } from '../models/cart.model';
import { Product } from '../models/product.model';
import { Service } from '../models/service.model';

const ITEMS_KEY = 'wc_cart_items';
const SERVICE_ITEMS_KEY = 'wc_cart_service_items';

@Injectable({ providedIn: 'root' })
export class CartService {
  /** منتجات السلة — بتنحفظ تلقائيًا بـ localStorage عند أي تغيير */
  readonly items = signal<CartItem[]>(this.readStored(ITEMS_KEY));

  /** خدمات مرتبطة بمنتجات (قص/كبس...) — منفصلة عن المنتجات لأنها كيان مختلف بالطلب */
  readonly serviceItems = signal<CartServiceItem[]>(this.readStored(SERVICE_ITEMS_KEY));

  readonly itemCount = computed(
    () =>
      this.items().reduce((sum, i) => sum + i.quantity, 0) +
      this.serviceItems().reduce((sum, i) => sum + i.quantity, 0)
  );

  readonly productsTotal = computed(() =>
    this.items().reduce((sum, i) => sum + i.price * i.quantity, 0)
  );

  readonly servicesTotal = computed(() =>
    this.serviceItems().reduce((sum, i) => sum + i.price * i.quantity, 0)
  );

  readonly totalPrice = computed(() => this.productsTotal() + this.servicesTotal());

  readonly isEmpty = computed(() => this.items().length === 0 && this.serviceItems().length === 0);

  constructor() {
    effect(() => localStorage.setItem(ITEMS_KEY, JSON.stringify(this.items())));
    effect(() => localStorage.setItem(SERVICE_ITEMS_KEY, JSON.stringify(this.serviceItems())));
  }

  // ---------- منتجات ----------
  addItem(product: Product, quantity = 1): void {
    const existing = this.items().find((i) => i.productId === product.id);

    if (existing) {
      this.updateQuantity(product.id, existing.quantity + quantity);
      return;
    }

    const primaryImage = product.images?.[0]?.imageUrl;
    this.items.update((list) => [
      ...list,
      {
        productId: product.id,
        name: product.name,
        price: product.price,
        quantity: Math.max(1, Math.min(quantity, product.stock)),
        stock: product.stock,
        imageUrl: primaryImage,
      },
    ]);
  }

  updateQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeItem(productId);
      return;
    }
    this.items.update((list) =>
      list.map((i) =>
        i.productId === productId ? { ...i, quantity: Math.min(quantity, i.stock) } : i
      )
    );
  }

  removeItem(productId: number): void {
    this.items.update((list) => list.filter((i) => i.productId !== productId));
  }

  // ---------- خدمات ----------
  addServiceItem(product: Product, service: Service, quantity = 1): void {
    const existing = this.serviceItems().find(
      (i) => i.productId === product.id && i.serviceId === service.id
    );

    if (existing) {
      this.updateServiceItemQuantity(product.id, service.id, existing.quantity + quantity);
      return;
    }

    this.serviceItems.update((list) => [
      ...list,
      {
        productId: product.id,
        productName: product.name,
        serviceId: service.id,
        serviceName: service.name,
        price: service.price,
        quantity: Math.max(1, quantity),
      },
    ]);
  }

  updateServiceItemQuantity(productId: number, serviceId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeServiceItem(productId, serviceId);
      return;
    }
    this.serviceItems.update((list) =>
      list.map((i) =>
        i.productId === productId && i.serviceId === serviceId ? { ...i, quantity } : i
      )
    );
  }

  removeServiceItem(productId: number, serviceId: number): void {
    this.serviceItems.update((list) =>
      list.filter((i) => !(i.productId === productId && i.serviceId === serviceId))
    );
  }

  // ---------- عام ----------
  clear(): void {
    this.items.set([]);
    this.serviceItems.set([]);
  }

  private readStored<T>(key: string): T[] {
    try {
      const raw = localStorage.getItem(key);
      return raw ? (JSON.parse(raw) as T[]) : [];
    } catch {
      return [];
    }
  }
}
