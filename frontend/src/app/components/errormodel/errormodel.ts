import { Component, Input, OnChanges, SimpleChanges, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-error-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './errormodel.html',
  styleUrl: './errormodel.css'
})
export class ErrorToastComponent implements OnInit, OnDestroy {
  @Input() message: string = '';
  @Input() show: boolean = false;
  @Input() duration: number = 5000; // Default 5 seconds
  
  private timeoutId: any;
  isVisible: boolean = false;

  ngOnInit() {
    // Watch for changes in the show input
    if (this.show && this.message) {
      this.displayToast();
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['show'] && changes['show'].currentValue && this.message) {
      this.displayToast();
    }
  }

  ngOnDestroy() {
    if (this.timeoutId) {
      clearTimeout(this.timeoutId);
    }
  }

  private displayToast() {
    this.isVisible = true;
    
    // Auto-hide after specified duration
    if (this.timeoutId) {
      clearTimeout(this.timeoutId);
    }
    
    this.timeoutId = setTimeout(() => {
      this.hideToast();
    }, this.duration);
  }

  hideToast() {
    this.isVisible = false;
    if (this.timeoutId) {
      clearTimeout(this.timeoutId);
      this.timeoutId = null;
    }
  }

  // Method to manually show toast (can be called from parent component)
  showToast(message: string, duration?: number) {
    this.message = message;
    this.duration = duration || this.duration;
    this.displayToast();
  }
}
