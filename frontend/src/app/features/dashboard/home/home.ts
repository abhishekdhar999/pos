import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import {ToastService} from '../../../components/errormodel/toast.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    RouterLink,
    CommonModule
  ],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {

  constructor(private toastService: ToastService){

  }
  showErrorToast() {
    this.toastService.error('Failed to Go To Clients');
  }

  showSuccessToast() {
    this.toastService.success('Failed to Go To Clients');
  }

  showWarningToast() {

  }

  showInfoToast() {
    this.toastService.info('Welcome to the POS Management System! Navigate through the different sections using the cards above.');
  }
}
