import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {OrderFilter} from '../models/order-filter.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiURLCreateClient = "http://localhost:8080/pos/api/clients/create"
  private apiURLGetClients = "http://localhost:8080/pos/api/clients"
  private apiURLUpdateClient ="http://localhost:8080/pos/api/clients/update"
private apiurl ='http://localhost:8080/pos/api'

  constructor(private http : HttpClient) {
  }


  getOrders(filter: OrderFilter): Observable<any> {
    return this.http.get('http://localhost:8080/pos/api/order', {
      params: {
        page: filter.page.toString(),
        size: filter.size.toString(),
        startDate: filter.startDate,
        endDate: filter.endDate,
        status: filter.status || '',
        orderId: filter.orderId ? filter.orderId.toString() : ''
      },
      withCredentials: true
    });
  }

  createOrder(orderData: any): Observable<any> {
    return this.http.post('http://localhost:8080/pos/api/order/create', orderData, {
      withCredentials: true
    });
  }

  generateInvoice(orderId: number): Observable<Blob> {
    return this.http.put(`${this.apiurl}/invoice/generate/${orderId}`, {}, {  // ← Change to PUT
      responseType: 'blob',
      withCredentials: true
    });
  }



  downloadInvoice(orderId: number): Observable<Blob> {
    return this.http.get(`http://localhost:8080/pos/api/invoice/download/${orderId}`, {
      responseType: 'blob',
      withCredentials: true
    });
  }

  resyncOrder(orderId: number): Observable<any> {
    return this.http.post(`http://localhost:8080/pos/api/order/resync/${orderId}`, {}, {
      withCredentials: true
    });
  }

}
