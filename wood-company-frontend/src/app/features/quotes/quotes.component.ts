import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { QuoteService } from '../../core/services/quote.service';
import { ProductService } from '../../core/services/product.service';
import { QuoteResponse } from '../../core/models/quote.model';
import { Product } from '../../core/models/product.model';
import { QuoteStatusLabelPipe } from '../../shared/pipes/quote-status-label.pipe';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-quotes',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    QuoteStatusLabelPipe,
    LoadingSpinnerComponent,
    EmptyStateComponent,
  ],
  templateUrl: './quotes.component.html',
  styleUrl: './quotes.component.scss',
})
export class QuotesComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly quoteService = inject(QuoteService);
  private readonly productService = inject(ProductService);

  readonly quotes = signal<QuoteResponse[]>([]);
  readonly products = signal<Product[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);

  readonly submitting = signal(false);
  readonly submitError = signal<string | null>(null);
  readonly submitSuccess = signal(false);

  readonly form = this.fb.nonNullable.group({
    productId: [null as number | null, Validators.required],
    quantity: [1, [Validators.required, Validators.min(1)]],
    message: [''],
  });

  ngOnInit(): void {
    this.productService.getAll().subscribe({ next: (list) => this.products.set(list) });
    this.loadQuotes();
  }

  private loadQuotes(): void {
    this.loading.set(true);
    this.quoteService.getAll().subscribe({
      next: (list) => {
        this.quotes.set([...list].sort((a, b) => b.id - a.id));
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('تعذّر تحميل طلبات عرض السعر.');
        this.loading.set(false);
      },
    });
  }

  statusClass(status: string): string {
    return `status-badge status-badge--${status.toLowerCase()}`;
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.submitError.set(null);
    this.submitSuccess.set(false);

    const raw = this.form.getRawValue();
    this.quoteService
      .create({
        productId: raw.productId as number,
        quantity: raw.quantity,
        message: raw.message || undefined,
      })
      .subscribe({
        next: () => {
          this.submitting.set(false);
          this.submitSuccess.set(true);
          this.form.reset({ productId: null, quantity: 1, message: '' });
          this.loadQuotes();
          setTimeout(() => this.submitSuccess.set(false), 2500);
        },
        error: (err: HttpErrorResponse) => {
          this.submitting.set(false);
          this.submitError.set(
            (err.error as { message?: string } | undefined)?.message ??
              'تعذّر إرسال الطلب، حاول مجددًا.'
          );
        },
      });
  }
}
