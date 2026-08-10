import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreateServiceRequest, Service, UpdateServiceRequest } from '../models/service.model';

// الاسم "ServiceApiService" بدل "ServiceService" لتفادي أي التباس
// مع مفهوم Angular Service نفسه (نفس القرار اللي اتخذناه بالباك اند).
@Injectable({ providedIn: 'root' })
export class ServiceApiService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<Service[]> {
    return this.http.get<Service[]>(`${environment.apiUrl}/services`);
  }

  create(payload: CreateServiceRequest): Observable<Service> {
    return this.http.post<Service>(`${environment.apiUrl}/services`, payload);
  }

  update(id: number, payload: UpdateServiceRequest): Observable<Service> {
    return this.http.put<Service>(`${environment.apiUrl}/services/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/services/${id}`);
  }
}
