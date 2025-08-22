// src/app/features/products/models/product.model.ts
export interface Product {
  id?: number;
  name: string;
  barcode : string;
  price: number;
  imageUrl : string;
  clientName:string;
}

