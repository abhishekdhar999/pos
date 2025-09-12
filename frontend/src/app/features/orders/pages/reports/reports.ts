import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {DatePipe, DecimalPipe} from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ToastService } from '../../../../components/errormodel/toast.service';
import {PaginatedResponse} from '../../services/sales-report.service';

export interface ReportsFilter {
  startDate: string;
  endDate: string;
  productBarcode?: string;
  client?: string;
  page?: number;
  size?: number;
}

export interface SalesData {
  productBarcode: string;
  client: string;
  quantity: number;
 revenue: number;
}

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [FormsModule, DecimalPipe, DatePipe],
  templateUrl: 'reports.html',
  styleUrl: 'reports.css'
})
export class ReportsComponent implements OnInit {
  loading = false;
  salesData: SalesData[] = [];
  totalPages = 0;
  currentPage = 0;

  // Export functionality
  isExporting = false;

  filter: ReportsFilter = {
    startDate: new Date(new Date().setDate(new Date().getDate() - 30))
        .toISOString()
        .split('T')[0],   // e.g. "2025-08-13"
      endDate: new Date().toISOString().split('T')[0],
    page: 0,
    size: 12
  };

  constructor(
    private http: HttpClient,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadReports();
  }

  loadReports(): void {
    this.loading = true;

    // Convert dates to ISO string format with timezone
    const startDate = new Date(this.filter.startDate + 'T00:00:00.000+05:30').toISOString();
    const endDate = new Date(this.filter.endDate + 'T23:59:59.999+05:30').toISOString();

    const params: any = {
      startDate,
      endDate,
      page: this.filter.page?.toString() || '0',
      size: this.filter.size?.toString() || '12'
    };

    // Add optional filters
    if (this.filter.productBarcode) {
      params.productBarcode = this.filter.productBarcode;
    }
    if (this.filter.client) {
      params.client = this.filter.client;
    }

    this.http.get<PaginatedResponse<SalesData>>('http://localhost:8080/pos/api/reports/sales', {
      params,
      withCredentials: true
    }).subscribe({
      next: (response:PaginatedResponse<SalesData>) => {
        console.log('Reports response:', response);
        console.log('Total pages:', response.totalPages);
        console.log('Current page:', response.page);
        console.log('Data length:', response.data.length);

        this.salesData = response.data;
        this.currentPage = response.page;
        this.totalPages = response.totalPages;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading reports', err);
        this.toastService.error('Failed to load reports');
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.filter.page = 0; // Reset to first page when applying filters
    this.loadReports();
  }

  clearFilters(): void {
    this.filter = {
      startDate: new Date(new Date().setDate(new Date().getDate() - 30))
          .toISOString()
          .split('T')[0],   // e.g. "2025-08-13"
        endDate: new Date().toISOString().split('T')[0],
      productBarcode: '',
      client: '',
      page: 0,
      size: 12
    };
    this.loadReports();
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.filter.page = (this.filter.page || 0) + 1;
      this.loadReports();
    }
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.filter.page = (this.filter.page || 0) - 1;
      this.loadReports();
    }
  }

  exportInventory(): void {
    this.isExporting = true;

    this.http.get('http://localhost:8080/pos/api/inventory/export', {
      responseType: 'text',
      withCredentials: true
    }).subscribe({
      next: (tsvContent: string) => {
        // Backend returns TSV content, so use it directly
        const blob = new Blob([tsvContent], { type: 'text/tab-separated-values' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `inventory-report-${new Date().toISOString().split('T')[0]}.tsv`;
        link.click();
        window.URL.revokeObjectURL(url);

        this.toastService.success('Inventory report exported successfully!');
        this.isExporting = false;
      },
      error: (err) => {
        console.error('Error exporting inventory report', err);
        this.toastService.error('Failed to export inventory report');
        this.isExporting = false;
      }
    });
  }
}
