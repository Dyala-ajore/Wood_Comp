import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * يُستخدم مع authGuard على نفس الـ route، ويتحقق إضافيًا إن دور المستخدم
 * ضمن الأدوار المسموحة. الأدوار تُمرَّر عبر route data:
 *
 *   { path: 'dashboard', canActivate: [authGuard, roleGuard], data: { roles: ['admin', 'manager'] } }
 *
 * ملاحظة مهمة: هاد التحقق واجهي فقط (UX) — الحماية الحقيقية موجودة أصلًا
 * بالباك اند عبر @PreAuthorize. حتى لو تلاعب حدا بالفرونت اند، السيرفر
 * رح يرفض الطلب على أي حال.
 */
export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const allowedRoles = (route.data['roles'] as string[] | undefined) ?? [];
  const userRole = auth.currentUser()?.role?.toLowerCase();

  if (userRole && allowedRoles.map((r) => r.toLowerCase()).includes(userRole)) {
    return true;
  }

  // مستخدم مسجّل دخول بس دوره مش مسموح → نرجّعه للرئيسية بدل صفحة لوج إن
  // (لأنه أصلًا مسجّل، المشكلة صلاحية مش هوية)
  return router.createUrlTree(['/']);
};
