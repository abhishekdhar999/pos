import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import { HttpClient } from '@angular/common/http';
import {Product} from '../models/product.model';
@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = 'http://localhost:8080/pos/api/product';

  constructor(private http: HttpClient) {}

  // getProducts(): Observable<Product[]> {
  //   return this.http.get<Product[]>(`${this.apiUrl}/all`);
  // }

  addProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  // updateProduct(id: number, product: Product): Observable<Product> {
  //   return this.http.put<Product>(`${this.apiUrl}/update/${id}`, product);
  // }
}
