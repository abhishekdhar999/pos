import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { SalesReportService, SalesReportFilter, SalesReportData, PaginatedResponse } from '../../services/sales-report.service';
import { ToastService } from '../../../../components/errormodel/toast.service';

@Component({
  selector: 'app-sales-report',
  standalone: true,
  imports: [FormsModule, DecimalPipe],
  templateUrl: 'sales-report.html',
  styleUrl: 'sales-report.css'
})
export class SalesReportComponent implements OnInit {
  loading = false;
  salesData: any[] = []
  // orders: any[] = [];
  totalPages = 0;
  currentPage = 0;

  filter: SalesReportFilter = {
      startDate: new Date(new Date().setDate(new Date().getDate() - 30))
          .toISOString()
          .split('T')[0],   // e.g. "2025-08-13"
        endDate: new Date().toISOString().split('T')[0],
    page: 0,
    size: 10
  };

  constructor(
    private salesReportService: SalesReportService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadSalesReport();
  }

  loadSalesReport(): void {
    this.loading = true;

    // Convert dates to ISO string format with timezone
    const startDate = new Date(this.filter.startDate + 'T00:00:00.000+05:30').toISOString();
    const endDate = new Date(this.filter.endDate + 'T23:59:59.999+05:30').toISOString();

    const reportFilter = {
      ...this.filter,
      startDate,
      endDate
    };

    this.salesReportService.getSalesReport(reportFilter).subscribe({
      next: (response: PaginatedResponse<SalesReportData>) => {
        this.salesData = response.data;
        this.totalPages = response.totalPages;
        this.currentPage = response.page;
        console.log("Sales report response:", response);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading sales report', err);
        this.toastService.error('Failed to load sales report');
        this.loading = false;
      }
    });
  }

  sum: any;
  item: any;

  applyFilters(): void {
    this.filter.page = 0;
    this.loadSalesReport();
  }

  nextPage(): void {
    if (this.filter.page < this.totalPages - 1) {
      this.filter.page++;
      this.loadSalesReport();
    }
  }

  prevPage(): void {
    if (this.filter.page > 0) {
      this.filter.page--;
      this.loadSalesReport();
    }
  }

  exportReport(): void {
    if (!this.filter.startDate || !this.filter.endDate) {
      this.toastService.error('Please select both start and end dates');
      return;
    }

    // Convert dates to ISO string format with timezone
    const startDate = new Date(this.filter.startDate + 'T00:00:00.000+05:30').toISOString();
    const endDate = new Date(this.filter.endDate + 'T23:59:59.999+05:30').toISOString();

    this.salesReportService.exportReport(startDate, endDate).subscribe({
      next: (tsvContent: string) => {
        // Backend returns TSV content, so use it directly
        const blob = new Blob([tsvContent], { type: 'text/tab-separated-values' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `sales-report-${this.filter.startDate}-to-${this.filter.endDate}.tsv`;
        link.click();
        window.URL.revokeObjectURL(url);

        this.toastService.success('Sales report exported successfully!');
      },
      error: (err) => {
        console.error('Error exporting sales report', err);
        this.toastService.error('Failed to export sales report');
      }
    });
  }

  getOrderTotal(order: any): number {
    if (order.orderItems && order.orderItems.length > 0) {
      return order.orderItems.reduce((total: number, item: any) =>
        total + (item.sellingPrice * item.quantity), 0);
    }
    return 0;
  }

  getDayOfWeek(dateString: string): string {
    try {
      // Extract the date part before the timezone info
      const datePart = dateString.split('T')[0];
      const date = new Date(datePart);
      const days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
      return days[date.getDay()];
    } catch (error) {
      return 'Unknown';
    }
  }
}
