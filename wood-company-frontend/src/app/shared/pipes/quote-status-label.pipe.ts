import { Pipe, PipeTransform } from '@angular/core';

const LABELS: Record<string, string> = {
  PENDING: 'قيد المراجعة',
  APPROVED: 'تمت الموافقة',
  REJECTED: 'مرفوض',
};

@Pipe({
  name: 'quoteStatusLabel',
  standalone: true,
})
export class QuoteStatusLabelPipe implements PipeTransform {
  transform(status: string): string {
    return LABELS[status] ?? status;
  }
}
