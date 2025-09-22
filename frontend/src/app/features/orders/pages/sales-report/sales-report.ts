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
today :string = "";
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
       const now = new Date();
              this.today = now.toISOString().split('T')[0];
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
    const today = new Date();
             console.log("today",today);
              const start = new Date(this.filter.startDate);
                   console.log("startDate",start);
              const end = new Date(this.filter.endDate);
                   console.log("endDate",end);


        // 1. End date cannot be in the future
          if (end > today) {
            this.toastService.error('End date cannot be in the future');
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
          this.toastService.error("start date cannot be in the future");
          return;
          }
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
  const today = new Date();
               console.log("today",today);
                const start = new Date(this.filter.startDate);
                     console.log("startDate",start);
                const end = new Date(this.filter.endDate);
                     console.log("endDate",end);


          // 1. End date cannot be in the future
            if (end > today) {
              this.toastService.error('End date cannot be in the future');
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
            this.toastService.error("start date cannot be in the future");
            return;
            }

    // Convert dates to ISO string format with timezone
    const startDate = new Date(this.filter.startDate + 'T00:00:00.000+05:30').toISOString();
    const endDate = new Date(this.filter.endDate + 'T23:59:59.999+05:30').toISOString();

    this.salesReportService.exportReport(startDate, endDate).subscribe({
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
          link.download = `daily-sales-report-${this.filter.startDate}-to-${this.filter.endDate}.pdf`;
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);

          this.toastService.success('Daily sales report exported as PDF successfully!');
        } catch (error) {
//           console.error('Error converting base64 to PDF:', error:any);
          this.toastService.error('Failed to convert report to PDF:' );
        }
      },
      error: (err) => {
        console.error('Error exporting sales report', err);
        this.toastService.error('Failed to export sales report: ' + (err.error?.error || err.message || 'Unknown error'));
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
