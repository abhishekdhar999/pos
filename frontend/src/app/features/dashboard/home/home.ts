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
  showInfoToast() {
    this.toastService.info('Welcome to POS Management System! Navigate through the cards to access different features.');
  }
}
