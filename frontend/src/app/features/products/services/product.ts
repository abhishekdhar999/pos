import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Product } from '../models/product.model';
import {Inventory} from '../models/inventory.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private addApiUrl = 'http://localhost:8080/pos/api/product';
  private getApiUrl = "http://localhost:8080/pos/api/product";
  private updateApiUrl = 'http://localhost:8080/pos/api/product';
  private bulkUploadUrl = 'http://localhost:8080/pos/api/product/upload';
private addInventoryUrl = "http://localhost:8080/pos/api/inventory/create"
  private bulkUploadInventoryUrl = "http://localhost:8080/pos/api/inventory/upload"
  constructor(private http: HttpClient) {}

  getProductsPaginated(page: number, size: number): Observable<any> {
    return this.http.get(
      `${this.getApiUrl}?page=${page}&size=${size}`,
      { withCredentials: true }
    );
  }

  addProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.addApiUrl, product, { withCredentials: true });
  }

  bulkUpload(productData: FormData): Observable<any> {
    return this.http.post(this.bulkUploadUrl, productData, { withCredentials: true });
  }

  updateProduct(id: number, product: Product): Observable<Product> {
  console.log("product",product);
    return this.http.put<Product>(`${this.updateApiUrl}/${id}`, product, { withCredentials: true });
  }

  addInventory(inventory:Inventory):Observable<Inventory>{
    return this.http.post<Inventory>(this.addInventoryUrl,inventory,{withCredentials:true});
  }

  bulkUploadInventory(inventoryData:FormData):Observable<any>{
  return this.http.put(this.bulkUploadInventoryUrl,inventoryData,{withCredentials:true})
  }

  getProductByBarcode(barcode: string): Observable<any> {
    return this.http.get(`${this.getApiUrl}/barcode/${barcode}`, { withCredentials: true });
  }
}
