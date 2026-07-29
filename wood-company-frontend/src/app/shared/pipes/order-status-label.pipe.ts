import { Pipe, PipeTransform } from '@angular/core';

const LABELS: Record<string, string> = {
  PENDING: 'قيد الانتظار',
  CONFIRMED: 'تم التأكيد',
  SHIPPED: 'تم الشحن',
  DELIVERED: 'تم التسليم',
  CANCELLED: 'ملغي',
};

@Pipe({
  name: 'orderStatusLabel',
  standalone: true,
})
export class OrderStatusLabelPipe implements PipeTransform {
  transform(status: string): string {
    return LABELS[status] ?? status;
  }
}
