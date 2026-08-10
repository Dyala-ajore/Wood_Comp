import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreateQuoteRequest, QuoteResponse, UpdateQuoteStatusRequest } from '../models/quote.model';

@Injectable({ providedIn: 'root' })
export class QuoteService {
  private readonly http = inject(HttpClient);

  create(payload: CreateQuoteRequest): Observable<QuoteResponse> {
    return this.http.post<QuoteResponse>(`${environment.apiUrl}/quote-requests`, payload);
  }

  /** بالنسبة للزبون: طلباته هو بس. بالنسبة للأدمن/المدير: كل الطلبات (نفس الـ endpoint، الباك اند بيفرز تلقائيًا حسب الدور) */
  getAll(): Observable<QuoteResponse[]> {
    return this.http.get<QuoteResponse[]>(`${environment.apiUrl}/quote-requests`);
  }

  updateStatus(id: number, payload: UpdateQuoteStatusRequest): Observable<QuoteResponse> {
    return this.http.put<QuoteResponse>(`${environment.apiUrl}/quote-requests/${id}`, payload);
  }
}
