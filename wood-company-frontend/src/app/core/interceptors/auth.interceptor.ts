import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * يضيف Authorization: Bearer <token> تلقائيًا على كل طلب،
 * بدل ما نكرر هالمنطق بكل Service مستقبلي (Products, Orders...).
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.getToken();

  const isAuthRequest =
      req.url.includes('/auth/login') ||
      req.url.includes('/auth/register');

  if (token && !isAuthRequest) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    });
  }

  return next(req);
};
