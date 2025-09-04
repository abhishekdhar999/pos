export interface OrderFilter {
  page: number;
  size: number;
  startDate: string;
  endDate: string;
  orderId?: number;
  status: string;
}
