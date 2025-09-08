import {Component, OnInit} from '@angular/core';
import {OrderService} from '../../services/order.service';
import {ToastService} from '../../../../components/errormodel/toast.service';
import {DatePipe} from '@angular/common';
import {OrderFilter} from '../../models/order-filter.model';
import {FormsModule} from '@angular/forms';
import {SafeStorageService} from '../../../auth/Services/SafeStorageService';
import {ProductService} from '../../../products/services/product';
import {Product} from '../../../products/models/product.model';
import {OrderItemForm} from '../../models/OrderItemForm';

interface CartItem {
  barcode: string;
  name: string;
  price: number;
  quantity: number;
}

@Component({
  selector: 'app-order-list',
  imports: [
    DatePipe,
    FormsModule
  ],
  templateUrl: './order-list.html',
  styleUrl: './order-list.css'
})
export class OrderList implements OnInit{

  orders: any[] = [];
  loading = true;
  totalPages =100;

  // Modal and cart properties
  showCreateOrderModal = false;
  availableProducts: Product[] = [];
  selectedBarcode = '';
  selectedProduct: Product | null = null;
  orderQuantity = 1;
  cartItems: CartItem[] = [];

  // Invoice generation properties
  generatingInvoice: number | null = null;

  // Resync properties
  resyncingAll = false;
  resyncingOrders = new Set<number>();

  constructor(
    private orderService: OrderService,
    private toastService: ToastService,
    private safeStorage: SafeStorageService,
    private productService: ProductService
  ) {}

  isSupervisor = false
  ngOnInit(): void {
    this.fetchOrders();
    const role = this.safeStorage.getItem('role'); // 👈 get role from sessionStorage
    this.isSupervisor = role === 'supervisor';
  }
  filter: OrderFilter = {
    page: 0,
    size: 10,
    startDate: '2025-07-15T10:47:38.803+05:30', // same as Constants.MIN_DATE
    endDate: new Date().toISOString(),
    orderId: undefined,
    status: ''
  };

  fetchOrders(): void {
    this.loading = true;
    this.orderService.getOrders(this.filter).subscribe({
      next: (data: any) => {
        console.log("data",data);
        this.orders = data;
        this.totalPages =100;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching orders', err);
        this.toastService.error('Failed to load orders');
        this.loading = false;
      }
    });
  }

  nextPage(): void {
    if (this.filter.page < this.totalPages - 1) {
      this.filter.page++;
      this.fetchOrders();
    }
  }

  prevPage(): void {
    if (this.filter.page > 0) {
      this.filter.page--;
      this.fetchOrders();
    }
  }

  applyFilters(): void {
    if (this.filter.startDate) {
      this.filter.startDate = new Date(this.filter.startDate).toISOString();
    }
    if (this.filter.endDate) {
      this.filter.endDate = new Date(this.filter.endDate).toISOString();
    }

    this.filter.page = 0; // reset pagination when applying new filters
    this.fetchOrders();
  }

  // Modal and cart methods
  openCreateOrderModal(): void {
    this.showCreateOrderModal = true;
    this.loadAvailableProducts();
    this.resetModal();
  }

  closeCreateOrderModal(): void {
    this.showCreateOrderModal = false;
    this.resetModal();
  }

  resetModal(): void {
    this.selectedBarcode = '';
    this.selectedProduct = null;
    this.orderQuantity = 1;
    this.cartItems = [];
  }

  loadAvailableProducts(): void {
    this.productService.getProductsPaginated(0, 1000).subscribe({
      next: (data: any) => {
        // The API returns a PaginatedResponse with data property
        const productDataList = data?.data || [];

        // Convert backend ProductData to frontend Product format
        this.availableProducts = productDataList.map((productData: any) => ({
          id: productData.id,
          name: productData.name,
          barcode: productData.barcode,
          price: productData.price, // Backend uses 'price'
          imageUrl: productData.imageUrl,
          clientName: productData.clientName,
          quantity: productData.quantity // Backend uses 'quantity'
        }));
      },
      error: (err) => {
        console.error('Error loading products', err);
        this.toastService.error('Failed to load products');
      }
    });
  }

  onBarcodeChange(): void {
    if (this.selectedBarcode) {
      // Fetch detailed product information by barcode
      this.productService.getProductByBarcode(this.selectedBarcode).subscribe({
        next: (productData: any) => {
          // Convert backend ProductData to frontend Product format
          this.selectedProduct = {
            id: productData.id,
            name: productData.name,
            barcode: productData.barcode,
            price: productData.price, // Backend uses 'price'
            imageUrl: productData.imageUrl,
            clientName: productData.clientName,
            quantity: productData.quantity // Backend uses 'quantity'
          };

          this.orderQuantity = 1;
        },
        error: (err) => {
          console.error('Error fetching product details:', err);
          this.toastService.error('Failed to load product details');
          this.selectedProduct = null;
        }
      });
    } else {
      this.selectedProduct = null;
    }
  }

  increaseQuantity(): void {
    if (this.selectedProduct && this.orderQuantity < this.selectedProduct.quantity) {
      this.orderQuantity++;
    }
  }

  decreaseQuantity(): void {
    if (this.orderQuantity > 1) {
      this.orderQuantity--;
    }
  }

  addToCart(): void {
    if (this.selectedProduct && this.orderQuantity > 0) {
      const existingItem = this.cartItems.find(item => item.barcode === this.selectedProduct!.barcode);

      if (existingItem) {
        existingItem.quantity += this.orderQuantity;
      } else {
        this.cartItems.push({
          barcode: this.selectedProduct.barcode,
          name: this.selectedProduct.name,
          price: this.selectedProduct.price,
          quantity: this.orderQuantity
        });
      }

      this.toastService.success(`Added ${this.orderQuantity} ${this.selectedProduct.name} to cart`);
      this.orderQuantity = 1;
    }
  }

  removeFromCart(barcode: string): void {
    this.cartItems = this.cartItems.filter(item => item.barcode !== barcode);
    this.toastService.success('Item removed from cart');
  }

  getCartTotal(): number {
    return this.cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);
  }

  createOrder(): void {
    if (this.cartItems.length === 0) {
      this.toastService.error('Cart is empty');
      return;
    }

    const orderData: OrderItemForm[] = this.cartItems.map(item => ({
      barcode: item.barcode,
      quantity: item.quantity,
      sellingPrice: item.price // make sure this exists in your frontend item object
    }));

    this.orderService.createOrder(orderData).subscribe({
      next: (response) => {
        this.toastService.success('Order created successfully');
        this.closeCreateOrderModal();
        this.fetchOrders(); // Refresh the order list
      },
      error: (err) => {
        console.error('Error creating order', err);
        this.toastService.error('Failed to create order');
      }
    });
  }

  generateInvoice(orderId: number): void {
    this.generatingInvoice = orderId;

    this.orderService.generateInvoice(orderId).subscribe({
      next: (blob: Blob) => {
        // Create a blob URL and trigger download
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `invoice-${orderId}.pdf`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);

        this.toastService.success('Invoice downloaded successfully');
        this.generatingInvoice = null;
      },
      error: (err) => {
        console.error('Error generating invoice', err);
        this.toastService.error('Failed to generate invoice');
        this.generatingInvoice = null;
      }
    });
  }

  resyncOrder(orderId: number): void {
    this.resyncingOrders.add(orderId);

    this.orderService.resyncOrder(orderId).subscribe({
      next: (response: any) => {
        this.toastService.success(`Order ${orderId} resynced successfully`);
        this.resyncingOrders.delete(orderId);
        this.fetchOrders(); // Refresh the order list to show updated status
      },
      error: (err) => {
        console.error('Error resyncing order', err);
        this.toastService.error(`Failed to resync order ${orderId}`);
        this.resyncingOrders.delete(orderId);
      }
    });
  }

  resyncAllOrders(): void {
    this.resyncingAll = true;

    // Resync all orders one by one
    const resyncPromises = this.orders.map(order =>
      this.orderService.resyncOrder(order.id).toPromise()
    );

    Promise.allSettled(resyncPromises).then(results => {
      const successful = results.filter(result => result.status === 'fulfilled').length;
      const failed = results.filter(result => result.status === 'rejected').length;

      if (successful > 0) {
        this.toastService.success(`${successful} orders resynced successfully`);
      }
      if (failed > 0) {
        this.toastService.error(`${failed} orders failed to resync`);
      }

      this.resyncingAll = false;
      this.fetchOrders(); // Refresh the order list
    });
  }

}
