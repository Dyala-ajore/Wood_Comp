import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * يحمي بس الصفحات اللي بتحتاج فعل فعلي (إتمام الطلب، طلباتي، لوحة التحكم).
 * التصفح العام (المنتجات، الخدمات) يبقى بدون Guard تمامًا.
 * لو المستخدم مش مسجّل، بيوجّهه للوج إن مع returnUrl، وبعد نجاح الدخول
 * بيرجعه تلقائيًا لنفس الصفحة اللي كان رايح لها.
 */
export const authGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isLoggedIn()) {
    return true;
  }

  return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
};
