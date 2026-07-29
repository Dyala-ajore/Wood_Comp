import { Pipe, PipeTransform } from '@angular/core';

/**
 * ينسّق الأرقام كسعر بالشيكل بشكل موحّد بكل الموقع
 * (بدل تكرار `{{ price }} ₪` بكل قالب لحاله).
 * الاستخدام: {{ product.price | ils }}
 */
@Pipe({
  name: 'ils',
  standalone: true,
})
export class IlsCurrencyPipe implements PipeTransform {
  transform(value: number | null | undefined): string {
    if (value == null) return '—';
    return `${value.toLocaleString('en-US')} ₪`;
  }
}
