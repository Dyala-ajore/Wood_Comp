import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../../core/services/dashboard.service';
import { DashboardResponse } from '../../core/models/dashboard.model';
import { IlsCurrencyPipe } from '../../shared/pipes/ils-currency.pipe';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';

interface StatCard {
  label: string;
  value: string;
  icon: string;
  tone: 'default' | 'warning' | 'brass';
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent, EmptyStateComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  private readonly dashboardService = inject(DashboardService);
  private readonly ilsPipe = new IlsCurrencyPipe();

  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly cards = signal<StatCard[]>([]);

  ngOnInit(): void {
    this.dashboardService.getSummary().subscribe({
      next: (data) => {
        this.cards.set(this.buildCards(data));
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('تعذّر تحميل إحصائيات لوحة التحكم.');
        this.loading.set(false);
      },
    });
  }

  private buildCards(d: DashboardResponse): StatCard[] {
    return [
      { label: 'إجمالي المبيعات', value: this.ilsPipe.transform(d.totalSales), icon: '₪', tone: 'brass' },
      { label: 'عدد الطلبات', value: `${d.totalOrders}`, icon: '📦', tone: 'default' },
      { label: 'طلبات معلّقة', value: `${d.pendingOrders}`, icon: '⏳', tone: 'warning' },
      { label: 'طلبات عرض سعر', value: `${d.totalQuoteRequests}`, icon: '📝', tone: 'default' },
      { label: 'عدد المنتجات', value: `${d.totalProducts}`, icon: 'خ', tone: 'default' },
      { label: 'مخزون منخفض', value: `${d.lowStockProductsCount}`, icon: '⚠', tone: 'warning' },
      { label: 'عدد المستخدمين', value: `${d.totalUsers}`, icon: '👤', tone: 'default' },
    ];
  }
}
