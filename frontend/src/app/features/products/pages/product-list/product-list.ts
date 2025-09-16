import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Product, ProductForm } from '../../models/product.model';
import { ProductService } from '../../services/product';
import { ToastService } from '../../../../components/errormodel/toast.service';
import {ClientService} from '../../../clients/services/client';
import {SafeStorageService} from '../../../auth/Services/SafeStorageService';
import {Inventory, InventoryForm} from '../../models/inventory.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './product-list.html',
  styleUrl: './product-list.css'
})
export class ProductList implements OnInit {


  uploadErrors: any[]  = [];
  uploadSuccesses: any[] = [];
  allUploadResults: any[] = []; // Combined results for download

  products: Product[] = [];
  loading = true;

  // pagination state
  page = 0;
  size = 9;
  totalPages = 0;

  // search state
  searchKeyword: string = '';
  searchResults: string[] = [];
  showSearchResults = false;

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
      quantity: [],
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
    console.log('Products - Fetching products for page:', this.page, 'size:', this.size, 'keyword:', this.searchKeyword);
    this.productService.getProductsPaginated(this.page, this.size, this.searchKeyword).subscribe({
      next: (data: any) => {
        console.log("Products - API response:", data);
        this.products = data.data;
        this.page = data.page;
        this.size = data.size;
        this.totalPages = data.totalPages;
        console.log('Products - Updated pagination - page:', this.page, 'totalPages:', this.totalPages, 'products count:', this.products.length);
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
    console.log('Products - Next page clicked. Current page:', this.page, 'Total pages:', this.totalPages);
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.fetchProducts();
    } else {
      console.log('Products - Cannot go to next page - already on last page');
    }
  }

  prevPage(): void {
    console.log('Products - Prev page clicked. Current page:', this.page, 'Total pages:', this.totalPages);
    if (this.page > 0) {
      this.page--;
      this.fetchProducts();
    } else {
      console.log('Products - Cannot go to prev page - already on first page');
    }
  }

  // Search functionality
  onSearchChange(): void {
    this.page = 0; // Reset to first page when searching
    this.fetchProducts();
    if(this.searchKeyword.trim().length > 2){
      this.productService.searchProductsByBarcode(0,10,this.searchKeyword).subscribe({
        next: (result:string[]) => {
          console.log("result products",result )
          this.searchResults = result;
          this.showSearchResults = true;
        },
        error: (err:any) => {
          console.error('Error searching products', err);
          this.toastService.error('Search failed');
        }
      })
    }else{
      this.showSearchResults = false;
      this.searchResults = [];
    }
  }

  selectSearchResult(barcode : string){
    this.searchKeyword  = barcode;
    this.showSearchResults = false;

    this.filterProductsByBarcode(barcode);
  }

  filterProductsByBarcode(barcode:string){
    this.loading = true;

    this.productService.getProductsPaginated(0,100,barcode).subscribe({
      next:(data : any)=>{
        this.products = data.data.filter((product: Product) =>
          product.barcode.toLowerCase().includes(barcode.toLowerCase())
        );
        this.loading = false;
    },
      error: (err) => {
        console.error('Error filtering products', err);
        this.loading = false;
      }
    })
  }

  clearSearch(): void {
    this.searchKeyword = '';
    this.showSearchResults = false;
    this.searchResults = [];
    this.page = 0;
    this.fetchProducts();
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
    // Clear upload results to hide download button
    this.uploadSuccesses = [];
    this.uploadErrors = [];
    this.allUploadResults = [];
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
    // Clear upload results to hide download button
    this.uploadSuccesses = [];
    this.uploadErrors = [];
    this.allUploadResults = [];
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
          const errorMessage = err.error?.error || err.error?.message || err.message || 'Failed to save product';
          this.toastService.error(errorMessage);
        }
      });
    // }
  }

  onSubmitInventory(): void{

//     if(this.inventoryForm.valid){
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
          this.toastService.error(err.error.error);
        }
      })

//     }else{
// //       console.log("errrrrr",err);
//       this.toastService.error(" ")
//       }
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

    // Parse TSV file and convert to ProductForm list
    this.parseTSVToProductList(this.selectedFile).then(productList => {
      console.log('Parsed product list:', productList);

      // Upload the list of ProductForm objects
      this.productService.bulkUploadProductList(productList).subscribe({
        next: (response: any[]) => {
          console.log("Product upload response:", response);

          // Check if response is an array of OperationResponse objects
          if (Array.isArray(response)) {
            const successfulItems = response.filter(item => item.message === "success");
            const failedItems = response.filter(item => item.message !== "No error");

            // Store results for download
            this.uploadSuccesses = successfulItems;
            this.uploadErrors = failedItems;
            this.allUploadResults = response; // Combined for comprehensive download

            // Show single info toast with both success and error counts
            this.toastService.info(`Upload completed: ${successfulItems.length} successful, ${failedItems.length} failed`);

            // If all items succeeded, close modal
            if (failedItems.length === 0) {
              this.closeBulkProductModal();
            }
          }

        this.selectedFile = null;
        this.fetchProducts();
      },
      error: (err: any) => {
          console.error('Error uploading products', err);
          this.toastService.error('Upload failed: ' + (err.error?.error || err.message || 'Unknown error'));
      }
      });
    }).catch(error => {
      console.error('Error parsing TSV file:', error);
      this.toastService.error('Error parsing file: ' + error.message);
    });
  }

  uploadBulkInventory(): void {
    if (!this.inventoryFile) {
      this.toastService.error('Please select a file');
      return;
    }

    // Parse TSV file and convert to InventoryForm list
    this.parseTSVToInventoryList(this.inventoryFile).then(inventoryList => {
      console.log('Parsed inventory list:', inventoryList);

      // Upload the list of InventoryForm objects
      this.productService.bulkUploadInventoryList(inventoryList).subscribe({
        next: (response: any[]) => {
          console.log("Inventory upload response:", response);

          // Check if response is an array of OperationResponse objects
          if (Array.isArray(response)) {
            const successfulItems = response.filter(item => item.message === "success");
            const failedItems = response.filter(item => item.message !== "success");

            // Store results for download
            this.uploadSuccesses = successfulItems;
            this.uploadErrors = failedItems;
            this.allUploadResults = response; // Combined for comprehensive download

            // Show single info toast with both success and error counts
            this.toastService.info(`Upload completed: ${successfulItems.length} successful, ${failedItems.length} failed`);

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
    }).catch(error => {
      console.error('Error parsing TSV file:', error);
      this.toastService.error('Error parsing file: ' + error.message);
    });
  }

  private parseTSVToProductList(file: File): Promise<ProductForm[]> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = (e) => {
        try {
          const text = e.target?.result as string;
          const lines = text.split('\n').filter(line => line.trim() !== '');

          if (lines.length < 2) {
            reject(new Error('File must contain at least a header row and one data row'));
            return;
          }

          // Parse header row
          const headers = lines[0].split('\t').map(h => h.trim().toLowerCase());
          console.log('Headers found:', headers);

          // Find column indices
          const nameIndex = headers.findIndex(h => h.includes('name'));
          const barcodeIndex = headers.findIndex(h => h.includes('barcode'));
          const priceIndex = headers.findIndex(h => h.includes('price'));
          const imageUrlIndex = headers.findIndex(h => h.includes('image') || h.includes('url'));
          const clientNameIndex = headers.findIndex(h => h.includes('client'));

//           if (nameIndex === -1 || barcodeIndex === -1 || priceIndex === -1 ||
//               imageUrlIndex === -1 || clientNameIndex === -1) {
//             reject(new Error('File must contain "name", "barcode", "price", "imageUrl", and "clientName" columns'));
//             return;
//           }

          // Parse data rows
          const productList: ProductForm[] = [];

          for (let i = 1; i < lines.length; i++) {
            const columns = lines[i].split('\t');

//             if (columns.length < Math.max(nameIndex, barcodeIndex, priceIndex, imageUrlIndex, clientNameIndex) + 1) {
//               console.warn(`Skipping row ${i + 1}: insufficient columns`);
//               continue;
//             }

            const name = columns[nameIndex]?.trim();
            const barcode = columns[barcodeIndex]?.trim();
            const priceStr = columns[priceIndex]?.trim();
            const imageUrl = columns[imageUrlIndex]?.trim();
            const clientName = columns[clientNameIndex]?.trim();

//             if (!name || !barcode || !priceStr || !imageUrl || !clientName) {
//               console.warn(`Skipping row ${i + 1}: missing required fields`);
//               continue;
//             }

             const price = parseFloat(priceStr);
//             if (isNaN(price)) {
//               console.warn(`Skipping row ${i + 1}: invalid price "${priceStr}"`);
//               continue;
//             }

            productList.push({
              name: name,
              barcode: barcode,
              price: price,
              imageUrl: imageUrl,
              clientName: clientName
            });
          }

//           if (productList.length === 0) {
//             reject(new Error('No valid product data found in file'));
//             return;
//           }

          console.log(`Successfully parsed ${productList.length} products`);
          resolve(productList);
        } catch (error) {
          reject(new Error('Error parsing file: ' + (error as Error).message));
        }
      };

      reader.onerror = () => {
        reject(new Error('Error reading file'));
      };

      reader.readAsText(file);
    });
  }

  private parseTSVToInventoryList(file: File): Promise<InventoryForm[]> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = (e) => {
        try {
          const text = e.target?.result as string;
          const lines = text.split('\n').filter(line => line.trim() !== '');

          if (lines.length < 2) {
            reject(new Error('File must contain at least a header row and one data row'));
            return;
          }

          // Parse header row
          const headers = lines[0].split('\t').map(h => h.trim().toLowerCase());
          console.log('Headers found:', headers);

          // Find column indices
          const barcodeIndex = headers.findIndex(h => h.includes('barcode'));
          const quantityIndex = headers.findIndex(h => h.includes('quantity'));
//
//           if (barcodeIndex === -1 || quantityIndex === -1) {
//             reject(new Error('File must contain "barcode" and "quantity" columns'));
//             return;
//           }

          // Parse data rows
          const inventoryList: InventoryForm[] = [];

          for (let i = 1; i < lines.length; i++) {
            const columns = lines[i].split('\t');

//             if (columns.length < Math.max(barcodeIndex, quantityIndex) + 1) {
//               console.warn(`Skipping row ${i + 1}: insufficient columns`);
//               continue;
//             }

            const barcode = columns[barcodeIndex]?.trim();
            const quantityStr = columns[quantityIndex]?.trim();

//             if (!barcode || !quantityStr) {
//               console.warn(`Skipping row ${i + 1}: missing barcode or quantity`);
//               continue;
//             }

            const quantity = parseInt(quantityStr, 10);
//             if (isNaN(quantity)) {
//               console.warn(`Skipping row ${i + 1}: invalid quantity "${quantityStr}"`);
//               continue;
//             }

            inventoryList.push({
              barcode: barcode,
              quantity: quantity
            });
          }

//           if (inventoryList.length === 0) {
//             reject(new Error('No valid inventory data found in file'));
//             return;
//           }

//           console.log(`Successfully parsed ${inventoryList.length} inventory items`);
          resolve(inventoryList);
        } catch (error) {
          reject(new Error('Error parsing file: ' + (error as Error).message));
        }
      };

      reader.onerror = () => {
        reject(new Error('Error reading file'));
      };

      reader.readAsText(file);
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
        const errorMessage = err.error?.error || err.error?.message || err.message || 'Failed to update product';
        this.toastService.error(errorMessage);
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

//   download errors and successes
  downloadErrors(): void {
    if (!this.allUploadResults || this.allUploadResults.length === 0) {
      this.toastService.error('No upload results to download');
      return;
    }

    // Check if this is inventory results (has barcode and quantity) or product results
    const isInventoryResult = this.allUploadResults.some(result =>
      result.data && (result.data.barcode !== undefined || result.data.quantity !== undefined)
    );

    let header: string[];
    let rows: any[][];

    if (isInventoryResult) {
      // Inventory result format
      header = ['Row', 'Status', 'Message', 'Barcode', 'Quantity'];
      rows = this.allUploadResults.map((result, index) => [
        index + 1,
        result.message === "success" ? 'SUCCESS' : 'ERROR',
        `"${result.message || ''}"`,
        result.data?.barcode || '',
        result.data?.quantity || ''
      ]);
    } else {
      // Product result format
      header = ['Row', 'Status', 'Message', 'Name', 'Barcode', 'Price', 'ImageUrl', 'ClientName'];
      rows = this.allUploadResults.map((result, index) => [
        index + 1,
        result.message === "success" ? 'SUCCESS' : 'ERROR',
        `"${result.message || ''}"`,
        result.data?.name || '',
        result.data?.barcode || '',
        result.data?.price || '',
        result.data?.imageUrl || '',
        result.data?.clientName || ''
      ]);
    }

    const csvContent = [header, ...rows].map(e => e.join(',')).join('\n');

    // Create Blob and trigger download
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = window.URL.createObjectURL(blob);

    const a = document.createElement('a');
    a.href = url;
    a.download = isInventoryResult ? 'inventory-upload-results.csv' : 'product-upload-results.csv';
    a.click();

    window.URL.revokeObjectURL(url);
  }

  clearErrors(): void {
    this.uploadErrors = [];
    this.uploadSuccesses = [];
    this.allUploadResults = [];
  }

  downloadSampleProductFile(): void {
    const sampleData = [
      ['name', 'barcode', 'price', 'imageUrl', 'clientName'],
      ['Sample Product 1', 'BARCODE001', '99.99', 'https://example.com/image1.jpg', 'Client A'],
      ['Sample Product 2', 'BARCODE002', '149.50', 'https://example.com/image2.jpg', 'Client B'],
      ['Sample Product 3', 'BARCODE003', '79.00', 'https://example.com/image3.jpg', 'Client A']
    ];

    const tsvContent = sampleData.map(row => row.join('\t')).join('\n');

    const blob = new Blob([tsvContent], { type: 'text/tab-separated-values' });
    const url = window.URL.createObjectURL(blob);

    const link = document.createElement('a');
    link.href = url;
    link.download = 'sample-products.tsv';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);

    this.toastService.success('Sample TSV file downloaded successfully');
  }

  downloadSampleInventoryFile(): void {
    const sampleData = [
      ['barcode', 'quantity'],
      ['BARCODE001', '50'],
      ['BARCODE002', '25'],
      ['BARCODE003', '100'],
      ['BARCODE004', '75'],
      ['BARCODE005', '30']
    ];

    const tsvContent = sampleData.map(row => row.join('\t')).join('\n');

    const blob = new Blob([tsvContent], { type: 'text/tab-separated-values' });
    const url = window.URL.createObjectURL(blob);

    const link = document.createElement('a');
    link.href = url;
    link.download = 'sample-inventory.tsv';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);

    this.toastService.success('Sample inventory TSV file downloaded successfully');
  }

}



