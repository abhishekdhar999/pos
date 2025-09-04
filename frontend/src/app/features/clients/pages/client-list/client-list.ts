import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Client } from '../../models/client.model';
import { ClientService } from '../../services/client';
import { RouterLink } from '@angular/router';
import { ToastService } from '../../../../components/errormodel/toast.service';
import {SafeStorageService} from '../../../auth/Services/SafeStorageService';

@Component({
  selector: 'app-client-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, ReactiveFormsModule],
  templateUrl: './client-list.html',
  styleUrls: ['./client-list.css']
})
export class ClientList implements OnInit {
  clients: Client[] = [];
  loading = true;

  // pagination state
  page = 0;
  size = 10;
  totalPages = 0;

  selectedClient: Client | null = null;
  clientForm: FormGroup;
  showAddModal = false;

  constructor(
    private fb: FormBuilder,
    private clientService: ClientService,
    private toastService: ToastService,
    private safeStorage: SafeStorageService
  ) {
    this.clientForm = this.fb.group({
      name: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.clientForm.valid) {
      const clientData: Client = this.clientForm.value;

      // 👇 Use ClientService instead of HttpClient directly
      this.clientService.addClient(clientData).subscribe({
        next: (res: any) => {
          console.log('Saved Client:', res);
          this.toastService.success('Client saved successfully!');
          this.clientForm.reset();
          this.closeAddModal(); // Close modal after successful save
          this.fetchClients(); // Refresh the list
        },
        error: (err: any) => {
          console.error('Error saving client:', err);
          const errorMessage = err.error?.message || 'Error saving client. Please try again.';
          this.toastService.error(errorMessage);
        }
      });
    }
  }

  isSupervisor = false;

  ngOnInit(): void {
    this.fetchClients();
    const role = this.safeStorage.getItem('role'); // 👈 get role from sessionStorage
    this.isSupervisor = role === 'supervisor';
  }

  fetchClients(): void {
    this.loading = true;
    this.clientService.getClientsPaginated(this.page, this.size).subscribe({
      next: (data: any) => {
        this.clients = data.data;
        this.page = data.page;
        this.size = data.size;
        this.totalPages = data.totalPages;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error fetching clients', err);
        this.toastService.error('Failed to load clients');
        this.loading = false;
      }
    });
  }

  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.fetchClients();
    }
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      this.fetchClients();
    }
  }

  openAddModal() {
    console.log('Opening add modal, showAddModal:', this.showAddModal);
    this.showAddModal = true;
    console.log('After setting showAddModal:', this.showAddModal);
  }

  closeAddModal() {
    console.log('Closing add modal, showAddModal:', this.showAddModal);
    this.showAddModal = false;
    console.log('After setting showAddModal:', this.showAddModal);
  }

  openUpdateModal(client: Client): void {
    console.log('Opening update modal for client:', client);
    this.selectedClient = { ...client };
  }

  closeModal(): void {
    console.log('Closing update modal');
    this.selectedClient = null;
  }

  updateClient(): void {
    if (!this.selectedClient) return;
    this.clientService.updateClient(this.selectedClient.id!, this.selectedClient).subscribe({
      next: () => {
        this.toastService.success('Client updated successfully!');
        this.closeModal();
        this.fetchClients();
      },
      error: (err) => {
        console.error('Error updating client', err);
        this.toastService.error(err.error.message);
      }
    });
  }
}
