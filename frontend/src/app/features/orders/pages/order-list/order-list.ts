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
  totalPages = 0;

  // Modal and cart properties
  showCreateOrderModal = false;
  availableProducts: Product[] = [];
  selectedBarcode = '';
  selectedProduct: Product | null = null;
  orderQuantity = 1;
  cartItems: CartItem[] = [];

  // Invoice generation properties
  generatingInvoice: number | null = null;
  downloadingInvoice: number | null = null;
  orderInvoiceIds: Map<number, number> = new Map(); // orderId -> invoiceId

  // Resync properties
  resyncingAll = false;
  resyncingOrders = new Set<number>();

  // Order details modal properties
  showOrderItemsModal = false;
  selectedOrderId: number | null = null;
  orderDetails: any = null;
  orderItems: any[] = [];
  loadingOrderItems = false;

  // Expandable orders
  expandedOrders = new Set<number>();

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
     startDate: new Date(new Date().setDate(new Date().getDate() - 30))
         .toISOString()
         .split('T')[0],
       endDate: new Date().toISOString().split('T')[0],
    orderId: undefined,
    status: ''
  };

  fetchOrders(): void {
    this.loading = true;

    // Validate and set default dates if invalid
    if (!this.filter.startDate || isNaN(new Date(this.filter.startDate).getTime())) {
      this.filter.startDate = new Date(new Date().setDate(new Date().getDate() - 30))
        .toISOString()
        .split('T')[0];
    }

    if (!this.filter.endDate || isNaN(new Date(this.filter.endDate).getTime())) {
      this.filter.endDate = new Date().toISOString().split('T')[0];
    }


    const startDate = new Date(this.filter.startDate + 'T00:00:00.000+05:30').toISOString();
    const endDate = new Date(this.filter.endDate + 'T23:59:59.999+05:30').toISOString();


    const OrderFilter = {
      ...this.filter,
      startDate,
      endDate
    };

    this.orderService.getOrders(OrderFilter).subscribe({
      next: (response: any) => {
        console.log("Order data received:", response);

        // Handle both array response and paginated response
        if (Array.isArray(response)) {
          this.orders = response;
          this.totalPages = Math.ceil(response.length / this.filter.size);
          console.log('Array response - totalPages calculated:', this.totalPages);
        } else if (response.data) {
          this.orders = response.data;
          this.totalPages = response.totalPages || Math.ceil(response.totalElements / this.filter.size);
          console.log('Paginated response - totalPages:', this.totalPages, 'totalElements:', response.totalElements);
        } else {
          this.orders = [];
          this.totalPages = 0;
          console.log('Empty response - totalPages set to 0');
        }

        // Log each order's isInvoiced status
        this.orders.forEach((order: any, index: number) => {
          console.log(`Order ${index + 1} (ID: ${order.id}): isInvoiced = ${order.isInvoiced}, status = ${order.status}`);
        });

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
    console.log('Next page clicked. Current page:', this.filter.page, 'Total pages:', this.totalPages);
    if (this.filter.page < this.totalPages - 1) {
      this.filter.page++;
      this.fetchOrders();
    } else {
      console.log('Cannot go to next page - already on last page');
    }
  }

  prevPage(): void {
    console.log('Prev page clicked. Current page:', this.filter.page, 'Total pages:', this.totalPages);
    if (this.filter.page > 0) {
      this.filter.page--;
      this.fetchOrders();
    } else {
      console.log('Cannot go to prev page - already on first page');
    }
  }

  applyFilters(): void {
     const today = new Date();
     console.log("today",today);
      const start = new Date(this.filter.startDate);
           console.log("startDate",start);
      const end = new Date(this.filter.endDate);
           console.log("endDate",end);

// 1. End date cannot be in the future
  if (end > today) {
    this.toastService.error('End date cannot be greater than today');
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
  this.toastService.error("start date can not be greater than today");
  return;
  }
    if (this.filter.startDate) {
      this.filter.startDate = new Date(this.filter.startDate).toISOString().split('T')[0];
    }
    if (this.filter.endDate) {
      this.filter.endDate = new Date(this.filter.endDate).toISOString().split('T')[0];
    }

    this.filter.page = 0; // reset pagination when applying new filters
    this.fetchOrders();
  }


//   clearFilters(): void {
//     this.filter = {
//       startDate: new Date(new Date().setDate(new Date().getDate() - 30))
//           .toISOString()
//           .split('T')[0],   // e.g. "2025-08-13"
//         endDate: new Date().toISOString().split('T')[0],
//       productBarcode: '',
//     status: '',
//     ordeId:''
//       page: 0,
//       size: 12
//     };
//     this.loadReports();
//   }
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

      // Clear selected product and reset form
      this.selectedProduct = null;
      this.selectedBarcode = '';
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
      next: (response: any) => {
        // Store the invoice ID for this order
        console.log("Invoice generation response:", response)
        if (response.invoiceId) {
          this.orderInvoiceIds.set(orderId, response.invoiceId);
        }

        this.toastService.success('Invoice generated successfully');
        this.generatingInvoice = null;

        // Add a small delay to ensure backend transaction is committed
        setTimeout(() => {
          console.log('Refreshing orders after invoice generation...');
          this.fetchOrders(); // Refresh the order list to update isInvoiced status
        }, 500);
      },
      error: (err) => {
        console.error('Error generating invoice', err);
        this.toastService.error('Failed to generate invoice');
        this.generatingInvoice = null;
      }
    });
  }

  downloadInvoice(orderId: number): void {
    this.downloadingInvoice = orderId;
    this.orderService.downloadInvoice(orderId).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `invoice-${orderId}.pdf`;
        link.click(); // 👈 no need to attach to body
        window.URL.revokeObjectURL(url);

        this.toastService.success('Invoice downloaded successfully');
        this.downloadingInvoice = null;
      },
      error: (err) => {
        console.error('Error downloading invoice', err);
        this.toastService.error('Failed to download invoice');
        this.downloadingInvoice = null;
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

  // resyncAllOrders(): void {
  //   this.resyncingAll = true;
  //
  //   // Resync all orders one by one
  //   const resyncPromises = this.orders.map(order =>
  //     this.orderService.resyncOrder(order.id).toPromise()
  //   );
  //
  //   Promise.allSettled(resyncPromises).then(results => {
  //     const successful = results.filter(result => result.status === 'fulfilled').length;
  //     const failed = results.filter(result => result.status === 'rejected').length;
  //
  //     if (successful > 0) {
  //       this.toastService.success(`${successful} orders resynced successfully`);
  //     }
  //     if (failed > 0) {
  //       this.toastService.error(`${failed} orders failed to resync`);
  //     }
  //
  //     this.resyncingAll = false;
  //     this.fetchOrders(); // Refresh the order list
  //   });
  // }

  // Order details modal methods
  viewOrderDetails(orderId: number): void {
    this.selectedOrderId = orderId;
    this.showOrderItemsModal = true;
    this.loadOrderDetails(orderId);
  }

  closeOrderItemsModal(): void {
    this.showOrderItemsModal = false;
    this.selectedOrderId = null;
    this.orderDetails = null;
    this.orderItems = [];
  }

  loadOrderDetails(orderId: number): void {
    this.loadingOrderItems = true;
    this.orderService.getOrderDetails(orderId).subscribe({
      next: (orderData: any) => {
        this.orderDetails = orderData;
        this.orderItems = orderData.orderItems || [];
        this.loadingOrderItems = false;
      },
      error: (err) => {
        console.error('Error loading order details', err);
        this.toastService.error('Failed to load order details');
        this.loadingOrderItems = false;
      }
    });
  }
  //
  // getOrderTotal(): number {
  //   return this.orderItems.reduce((total, item) =>
  //     total + (item.sellingPrice * item.quantity), 0);
  // }

  toggleOrderExpansion(orderId: number): void {
    if (this.expandedOrders.has(orderId)) {
      this.expandedOrders.delete(orderId);
    } else {
      this.expandedOrders.add(orderId);
      // Load order details if not already loaded
      if (!this.orders.find(order => order.id === orderId)?.orderItems) {
        this.loadOrderDetailsForExpansion(orderId);
      }
    }
  }

  loadOrderDetailsForExpansion(orderId: number): void {
    this.orderService.getOrderDetails(orderId).subscribe({
      next: (orderData: any) => {
        const order = this.orders.find(o => o.id === orderId);
        if (order) {
          order.orderItems = orderData.orderItems || [];
        }
      },
      error: (err) => {
        console.error('Error loading order details for expansion', err);
        this.toastService.error('Failed to load order details');
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

}
