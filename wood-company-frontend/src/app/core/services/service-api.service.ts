import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Service } from '../models/service.model';

// الاسم "ServiceApiService" بدل "ServiceService" لتفادي أي التباس
// مع مفهوم Angular Service نفسه (نفس القرار اللي اتخذناه بالباك اند).
@Injectable({ providedIn: 'root' })
export class ServiceApiService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<Service[]> {
    return this.http.get<Service[]>(`${environment.apiUrl}/services`);
  }
}
