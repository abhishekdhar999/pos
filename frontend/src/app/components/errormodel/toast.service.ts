import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface ToastMessage {
  id: string;
  message: string;
  type: 'error' | 'success' | 'warning' | 'info';
  duration?: number;
  timestamp: number;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toasts = new BehaviorSubject<ToastMessage[]>([]);
  public toasts$ = this.toasts.asObservable();

  constructor() {
    console.log('ToastService initialized');
  }

  // Show error toast
  error(message: string, duration: number = 5000): string {
    console.log('ToastService.error called with:', message);
    return this.showToast(message, 'error', duration);
  }

  // Show success toast
  success(message: string, duration: number = 3000): string {
    console.log('ToastService.success called with:', message);
    return this.showToast(message, 'success', duration);
  }

  // Show warning toast
  warning(message: string, duration: number = 4000): string {
    console.log('ToastService.warning called with:', message);
    return this.showToast(message, 'warning', duration);
  }

  // Show info toast
  info(message: string, duration: number = 4000): string {
    console.log('ToastService.info called with:', message);
    return this.showToast(message, 'info', duration);
  }

  // Generic method to show toast
  private showToast(message: string, type: 'error' | 'success' | 'warning' | 'info', duration: number): string {
    const id = this.generateId();
    const toast: ToastMessage = {
      id,
      message,
      type,
      duration,
      timestamp: Date.now()
    };

    console.log('Creating toast:', toast);
    const currentToasts = this.toasts.value;
    const newToasts = [...currentToasts, toast];
    console.log('Current toasts:', currentToasts);
    console.log('New toasts array:', newToasts);
    
    this.toasts.next(newToasts);

    // Auto-remove toast after duration
    setTimeout(() => {
      console.log('Auto-removing toast:', id);
      this.removeToast(id);
    }, duration);

    return id;
  }

  // Remove specific toast
  removeToast(id: string): void {
    console.log('Removing toast:', id);
    const currentToasts = this.toasts.value;
    const filteredToasts = currentToasts.filter(toast => toast.id !== id);
    console.log('Toasts after removal:', filteredToasts);
    this.toasts.next(filteredToasts);
  }

  // Remove all toasts
  clearAll(): void {
    console.log('Clearing all toasts');
    this.toasts.next([]);
  }

  // Generate unique ID for toast
  private generateId(): string {
    return 'toast_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
  }

  // Get current toasts
  getCurrentToasts(): ToastMessage[] {
    return this.toasts.value;
  }
}
