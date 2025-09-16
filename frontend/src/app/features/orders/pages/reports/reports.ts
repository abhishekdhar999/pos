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
    const today = new Date();
         console.log("today",today);
          const start = new Date(this.filter.startDate);
               console.log("startDate",start);
          const end = new Date(this.filter.endDate);
               console.log("endDate",end);


    // 1. End date cannot be in the future
      if (end > today) {
        this.toastService.error('End date cannot be greater than today');
        this.filter.endDate = today.toISOString().split('T')[0];
        return;
      }

      // 2. Start date must not be more than 30 days before end date
      const minStart = new Date(end);
      minStart.setDate(minStart.getDate() - 30);

      if (start < minStart) {
        this.toastService.error('Start date cannot be more than 30 days before end date');
        this.filter.startDate = minStart.toISOString().split('T')[0];
        return;
      }

    //3 start date should not be greater than today
    if(start > end){
      this.toastService.error("start date can not be greater than today");
      return;
      }
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
      next: (base64Content: string) => {
        // Backend returns PDF base64 string, convert to PDF
        try {
          // Check if response is valid base64
          if (!base64Content || base64Content.trim() === '') {
            throw new Error('Empty response from server');
          }

          // Remove data URL prefix if present
          const base64Data = base64Content.includes(',')
            ? base64Content.split(',')[1]
            : base64Content;

          // Convert base64 to binary
          const binaryString = atob(base64Data);
          const bytes = new Uint8Array(binaryString.length);

          for (let i = 0; i < binaryString.length; i++) {
            bytes[i] = binaryString.charCodeAt(i);
          }

          // Create PDF blob
          const blob = new Blob([bytes], { type: 'application/pdf' });
          const url = window.URL.createObjectURL(blob);

          // Create download link
          const link = document.createElement('a');
          link.href = url;
          link.download = `inventory-report-${new Date().toISOString().split('T')[0]}.pdf`;
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);

          this.toastService.success('Inventory report exported as PDF successfully!');
          this.isExporting = false;
        } catch (error) {
//           console.error('Error converting base64 to PDF:', error);
          this.toastService.error('Failed to convert inventory report to PDF: ');
          this.isExporting = false;
        }
      },
      error: (err) => {
        console.error('Error exporting inventory report', err);
        this.toastService.error('Failed to export inventory report: ' + (err.error?.error || err.message || 'Unknown error'));
        this.isExporting = false;
      }
    });
  }

  exportSales(): void {
    this.isExporting = true;

  if (!this.filter.startDate || !this.filter.endDate) {
      this.toastService.error('Please select both start and end dates');
      return;
    }

    // Convert dates to ISO string format with timezone
    const startDate = new Date(this.filter.startDate + 'T00:00:00.000+05:30').toISOString();
    const endDate = new Date(this.filter.endDate + 'T23:59:59.999+05:30').toISOString();
    // Build query parameters from current filter
    const params: any = {
      startDate,
      endDate,
      page: this.filter.page || 0,
      size: this.filter.size || 12
    };

    // Add optional parameters if they exist
    if (this.filter.productBarcode) {
      params.productBarcode = this.filter.productBarcode;
    }
    if (this.filter.client) {
      params.client = this.filter.client;
    }

    this.http.get('http://localhost:8080/pos/api/reports/export-sales', {
      params: params,
      responseType: 'text',
      withCredentials: true
    }).subscribe({
      next: (base64Content: string) => {
        // Backend returns PDF base64 string, convert to PDF
        try {
          // Check if response is valid base64
          if (!base64Content || base64Content.trim() === '') {
            throw new Error('Empty response from server');
          }

          // Remove data URL prefix if present
          const base64Data = base64Content.includes(',')
            ? base64Content.split(',')[1]
            : base64Content;

          // Convert base64 to binary
          const binaryString = atob(base64Data);
          const bytes = new Uint8Array(binaryString.length);

          for (let i = 0; i < binaryString.length; i++) {
            bytes[i] = binaryString.charCodeAt(i);
          }

          // Create PDF blob
          const blob = new Blob([bytes], { type: 'application/pdf' });
          const url = window.URL.createObjectURL(blob);

          // Create download link with date range in filename
          const startDate = this.filter.startDate ? this.filter.startDate.split('T')[0] : 'all';
          const endDate = this.filter.endDate ? this.filter.endDate.split('T')[0] : 'all';
          const link = document.createElement('a');
          link.href = url;
          link.download = `sales-report-${startDate}-to-${endDate}.pdf`;
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);

          this.toastService.success('Sales report exported as PDF successfully!');
          this.isExporting = false;
        } catch (error) {
//           console.error('Error converting base64 to PDF:', error);
          this.toastService.error('Failed to convert sales report to PDF: ' );
          this.isExporting = false;
        }
      },
      error: (err) => {
        console.error('Error exporting sales report', err);
        this.toastService.error('Failed to export sales report: ' + (err.error?.error || err.message || 'Unknown error'));
        this.isExporting = false;
      }
    });
  }
}
