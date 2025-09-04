import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Product } from '../../models/product.model';
import { ProductService } from '../../services/product';
import { ToastService } from '../../../../components/errormodel/toast.service';
import {ClientService} from '../../../clients/services/client';
import {SafeStorageService} from '../../../auth/Services/SafeStorageService';
import {Inventory} from '../../models/inventory.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './product-list.html',
  styleUrl: './product-list.css'
})
export class ProductList implements OnInit {


  uploadErrors: any[]  = [];

  products: Product[] = [];
  loading = true;

  // pagination state
  page = 0;
  size = 10;
  totalPages = 0;

  // Modal states
  showAddProductDropdown = false;
  showInventoryDropdown = false;
  showSingleProductModal = false;
  showBulkProductModal = false;
  showSingleInventoryModal = false;
  showBulkInventoryModal = false;

  // Selected items
  selectedProduct: Product | null = null;
  selectedFile: File | null = null;
  inventoryFile: File | null = null;

  // Forms
  productForm: FormGroup;
  inventoryForm: FormGroup;

  // Role-based access
  isSupervisor = false;


  constructor(
    private productService: ProductService,
    private toastService: ToastService,
    private fb: FormBuilder,
    private safeStorage: SafeStorageService
  ) {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(1)]],
      barcode: ['', Validators.required],
      imageUrl: ['', Validators.required],
      clientName: ['', Validators.required],
      quantity: [0, [Validators.required, Validators.min(0)]]
    });

    this.inventoryForm = this.fb.group({
      barcode: ['', Validators.required],
      quantity: [0, [Validators.required, Validators.min(0)]],
    });
  }

  ngOnInit(): void {
    this.fetchProducts();
    const role = this.safeStorage.getItem('role');
    this.isSupervisor = role === 'supervisor';
  }

  // Product fetching
  fetchProducts(): void {
    this.loading = true;
    this.productService.getProductsPaginated(this.page, this.size).subscribe({
      next: (data: any) => {
        console.log("data",data.data);
        this.products = data.data;
        this.page = data.page;
        this.size = data.size;
        this.totalPages = data.totalPages;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error fetching products', err);
        this.toastService.error('Failed to load products');
        this.loading = false;
      }
    });
  }

  // Pagination
  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.fetchProducts();
    }
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      this.fetchProducts();
    }
  }

  // Dropdown toggles
  toggleAddProductDropdown(): void {
    this.showAddProductDropdown = !this.showAddProductDropdown;
    this.showInventoryDropdown = false; // Close other dropdown
  }

  toggleInventoryDropdown(): void {
    this.showInventoryDropdown = !this.showInventoryDropdown;
    this.showAddProductDropdown = false; // Close other dropdown
  }

  // Product modals
  openSingleProductModal(): void {
    this.showSingleProductModal = true;
    this.showAddProductDropdown = false;
  }

  openBulkProductModal(): void {
    this.showBulkProductModal = true;
    this.showAddProductDropdown = false;
  }

  closeSingleProductModal(): void {
    this.showSingleProductModal = false;
    this.productForm.reset();
  }

  closeBulkProductModal(): void {
    this.showBulkProductModal = false;
    this.selectedFile = null;
    this.uploadErrors = [];
  }

  // Inventory modals
  openSingleInventoryModal(): void {
    this.showSingleInventoryModal = true;
    this.showInventoryDropdown = false;
  }

  openBulkInventoryModal(): void {
    this.showBulkInventoryModal = true;
    this.showInventoryDropdown = false;
  }

  closeSingleInventoryModal(): void {
    this.showSingleInventoryModal = false;
    this.inventoryForm.reset();
  }

  closeBulkInventoryModal(): void {
    this.showBulkInventoryModal = false;
    this.inventoryFile = null;
  }

  // Product operations
  onSubmitProduct(): void {
    // if (this.productForm.valid) {

      const productData: Product = this.productForm.value;
      this.productService.addProduct(productData).subscribe({
        next: (res: any) => {
          console.log('Saved Product:', res);
          this.toastService.success('Product saved successfully!');
          this.closeSingleProductModal();
          this.fetchProducts();
        },
        error: (err: any) => {
          console.error('Error saving product:', err);
          this.toastService.error(err.error.message);
        }
      });
    // }
  }

  onSubmitInventory(): void{
    if(this.inventoryForm.valid){
      const inventoryData:Inventory =this.inventoryForm.value;
      console.log("inventory",inventoryData)
      this.productService.addInventory(inventoryData).subscribe({
        next:(res:any)=>{
          console.log("res",res);
this.toastService.success("inventory saved successfully");
this.closeSingleInventoryModal()
this.fetchProducts();
        },
        error:(err:any) =>{
          console.log("err",err)
          this.toastService.error("error creating inventor please check credentials");
        }
      })

    }
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  onInventoryFileSelected(event: any): void {
    this.inventoryFile = event.target.files[0];
  }

  uploadBulkProducts(): void {
    if (!this.selectedFile) {
      this.toastService.error('Please select a file');
      return;
    }
    const productData = new FormData();
    productData.append('file', this.selectedFile);

    this.productService.bulkUpload(productData).subscribe({
      next: (response: any[]) => {
        console.log("Upload response:", response);

        // Check if response is an array of OperationResponse objects
        if (Array.isArray(response)) {
          const successfulItems = response.filter(item => item.message === "No error");
          const failedItems = response.filter(item => item.message !== "No error");

          // Show success message for successful items
          if (successfulItems.length > 0) {
            this.toastService.success(`${successfulItems.length} products uploaded successfully`);
          }

          // Show error messages for failed items
          if (failedItems.length > 0) {
            this.toastService.error(`${failedItems.length} products failed to upload`);
            this.uploadErrors = failedItems; // Store failed items for display
          }

          // If all items failed, show general error
          if (successfulItems.length === 0 && failedItems.length > 0) {
            this.toastService.error('Upload failed - all products had errors');
          }

          // If all items succeeded, close modal
          if (failedItems.length === 0) {
            this.closeBulkProductModal();
          }
        }

        this.selectedFile = null;
        this.fetchProducts();
      },
      error: (err: any) => {
        console.error('Error uploading products:', err);
        this.toastService.error('Upload failed: ' + (err.error?.error || err.message || 'Unknown error'));
      }
    });
  }

  uploadBulkInventory(): void {
    if (!this.inventoryFile) {
      this.toastService.error('Please select a file');
      return;
    }
    const inventoryData = new FormData();
    inventoryData.append('file', this.inventoryFile);

    this.productService.bulkUploadInventory(inventoryData).subscribe({
      next: (response: any[]) => {
        console.log("Inventory upload response:", response);
        
        // Check if response is an array of OperationResponse objects
        if (Array.isArray(response)) {
          const successfulItems = response.filter(item => item.message === "No error");
          const failedItems = response.filter(item => item.message !== "No error");
          
          // Show success message for successful items
          if (successfulItems.length > 0) {
            this.toastService.success(`${successfulItems.length} inventory items uploaded successfully`);
          }
          
          // Show error messages for failed items
          if (failedItems.length > 0) {
            this.toastService.error(`${failedItems.length} inventory items failed to upload`);
            this.uploadErrors = failedItems; // Store failed items for display
          }
          
          // If all items failed, show general error
          if (successfulItems.length === 0 && failedItems.length > 0) {
            this.toastService.error('Upload failed - all inventory items had errors');
          }
          
          // If all items succeeded, close modal
          if (failedItems.length === 0) {
            this.closeBulkInventoryModal();
          }
        }
        
        this.inventoryFile = null;
        this.fetchProducts();
      },
      error: (err: any) => {
        console.error('Error uploading inventory:', err);
        this.toastService.error('Upload failed: ' + (err.error?.error || err.message || 'Unknown error'));
      }
    });
  }



  // Update product
  openUpdateModal(product: Product): void {
    this.selectedProduct = { ...product };
  }

  closeUpdateModal(): void {
    this.selectedProduct = null;
  }

  updateProduct(): void {
    if (!this.selectedProduct) return;

    this.productService.updateProduct(this.selectedProduct.id!, this.selectedProduct).subscribe({
      next: () => {
        this.toastService.success('Product updated successfully!');
        this.closeUpdateModal();
        this.fetchProducts();
      },
      error: (err: any) => {
        console.error('Error updating product:', err);
        this.toastService.error(err.error.message);
      }
    });
  }

  // Close dropdowns when clicking outside
  onDropdownClick(event: Event): void {
    event.stopPropagation();
  }

  // Close all dropdowns
  closeAllDropdowns(): void {
    this.showAddProductDropdown = false;
    this.showInventoryDropdown = false;
  }

  onClientHover() {
    this.toastService.error("Client name cannot be changed!");
  }
  onBarcodeHover(){
    this.toastService.error("Barcode cannot be changed once set!");
  }

//   download errors
  downloadErrors(): void {
    if (!this.uploadErrors || this.uploadErrors.length === 0) {
      this.toastService.error('No errors to download');
      return;
    }

    // Convert errors to CSV string
    const header = ['Row', 'Message', 'Barcode', 'Client Name'];
    const rows = this.uploadErrors.map(err  => [
      err.row,
      `"${err.message}"`,         // wrap in quotes to avoid commas breaking
      err.form?.barcode ?? '',
      err.form?.clientName ?? ''
    ]);

    const csvContent =
      [header, ...rows].map(e => e.join(',')).join('\n');

    // Create Blob and trigger download
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = window.URL.createObjectURL(blob);

    const a = document.createElement('a');
    a.href = url;
    a.download = 'bulk-upload-errors.csv';
    a.click();

    window.URL.revokeObjectURL(url);
  }

  clearErrors(): void {
    this.uploadErrors = [];
  }

}



