import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    // كل الصفحات العامة (تصفح بدون تسجيل دخول) تعيش جوا الـ Shell
    path: '',
    loadComponent: () => import('./layout/shell/shell.component').then((m) => m.ShellComponent),
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./features/catalog/catalog.component').then((m) => m.CatalogComponent),
      },
      {
        path: 'products/:id',
        loadComponent: () =>
          import('./features/catalog/product-detail/product-detail.component').then(
            (m) => m.ProductDetailComponent
          ),
      },
      {
        path: 'services',
        loadComponent: () =>
          import('./features/services/services.component').then((m) => m.ServicesComponent),
      },
      {
        path: 'cart',
        loadComponent: () => import('./features/cart/cart.component').then((m) => m.CartComponent),
      },
      {
        // الصفحة الوحيدة المحمية بهالمرحلة: إتمام الطلب
        path: 'checkout',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/checkout/checkout.component').then((m) => m.CheckoutComponent),
      },
      {
        // طلباتي: محمية أيضًا، الزبون لازم يكون مسجّل دخول ليشوفها
        path: 'orders',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/orders/orders.component').then((m) => m.OrdersComponent),
      },
    ],
  },
  // اللوج إن والتسجيل: صفحات مستقلة بتصميمها الخاص (بدون الهيدر العام)
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then((m) => m.RegisterComponent),
  },
  { path: '**', redirectTo: '' },
];
