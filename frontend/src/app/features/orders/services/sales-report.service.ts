import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SalesReportFilter {
  startDate: string;
  endDate: string;
  page: number;
  size: number;
}

export interface SalesReportData {
  date: string;
  invoicedOrdersCount: number;
  invoicedItemsCount: number;
  totalRevenue: number;
}

export interface PaginatedResponse<T> {
  data: T[];
  page: number;
  size: number;
  totalPages: number;
}

@Injectable({
  providedIn: 'root'
})
export class SalesReportService {
  private baseUrl = 'http://localhost:8080/pos/api';

  constructor(private http: HttpClient) {}

  getSalesReport(filter: SalesReportFilter): Observable<PaginatedResponse<SalesReportData>> {
    const params = {
      startDate: filter.startDate,
      endDate: filter.endDate,
      page: filter.page?.toString() || '0',
      size: filter.size?.toString() || '10'
    };

    return this.http.get<PaginatedResponse<SalesReportData>>(`${this.baseUrl}/reports/daily`, { params ,withCredentials: true},);
  }

  exportReport(startDate: string, endDate: string): Observable<string> {
    const params = {
      startDate: startDate,
      endDate: endDate
    };

    return this.http.get(`${this.baseUrl}/reports/export`, {
      params,
      responseType: 'text',
      withCredentials: true
    });
  }
}
