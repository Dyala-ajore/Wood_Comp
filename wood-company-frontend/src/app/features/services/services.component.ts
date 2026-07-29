import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ServiceApiService } from '../../core/services/service-api.service';
import { Service } from '../../core/models/service.model';
import { IlsCurrencyPipe } from '../../shared/pipes/ils-currency.pipe';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-services',
  standalone: true,
  imports: [CommonModule, RouterLink, IlsCurrencyPipe, LoadingSpinnerComponent, EmptyStateComponent],
  templateUrl: './services.component.html',
  styleUrl: './services.component.scss',
})
export class ServicesComponent implements OnInit {
  private readonly serviceApi = inject(ServiceApiService);

  readonly services = signal<Service[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.serviceApi.getAll().subscribe({
      next: (list) => {
        this.services.set(list);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('تعذّر تحميل الخدمات، حاول لاحقًا.');
        this.loading.set(false);
      },
    });
  }
}
