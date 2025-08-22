import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProductService } from '../../services/product';
import { Product } from '../../models/product.model';
import {Client} from '../../../clients/models/client.model';
import {ClientService} from '../../../clients/services/client';  // ✅ Import Product interface

@Component({
  selector: 'app-product-form',
  standalone: true,   // ✅ Needed for Angular 15+
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './product-form.html',
  styleUrls: ['./product-form.css']   // ✅ Corrected
})
export class ProductForm implements OnInit{
  productForm: FormGroup;
  clients: Client[] = [];

  constructor(private fb: FormBuilder, private productService: ProductService,private clientService: ClientService) {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(1)]],
      barcode: ['', Validators.required],
      imageUrl: ['', Validators.required],
      clientName: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    // Fetch clients for dropdown
    this.clientService.getClients().subscribe({
      next: (data) => (this.clients = data),
      error: (err) => console.error('Failed to load clients:', err)
    });
  }
  onSubmit(): void {   // ✅ Corrected
    if (this.productForm.valid) {
      const productData: Product = this.productForm.value;  // ✅ Get form values

      this.productService.addProduct(productData).subscribe({
        next: (res: any) => {
          console.log('Saved Product:', res);
          alert('Product saved successfully!');
          this.productForm.reset();
        },
        error: (err: any) => {
          console.error('Error saving product:', err);
          alert('Error while saving product');
        }
      });
    }
  }
}
