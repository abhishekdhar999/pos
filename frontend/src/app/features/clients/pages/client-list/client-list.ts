
import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // 👈 needed for ngModel
import { Client } from '../../models/client.model';
import { ClientService } from '../../services/client';

@Component({
  selector: 'app-client-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './client-list.html',
  styleUrls: ['./client-list.css']
})
export class ClientList implements OnInit{
  clients: Client[] = [];
  loading = true;
  error = '';
  selectedClient: Client | null = null; // 👈 holds client being edited

  constructor(private clientService: ClientService) {}

  ngOnInit(): void {
    this.fetchClients();
  }

  fetchClients(): void {
    this.clientService.getClients().subscribe({
      next: (data:any) => {
        this.clients = data;
        this.loading = false;
      },
      error: (err:any) => {
        console.error('Error fetching clients', err);
        this.error = 'Failed to load clients';
        this.loading = false;
      }
    });
  }

  openUpdateModal(client: Client): void {
    // clone object to avoid changing directly before save
    this.selectedClient = { ...client };
  }

  closeModal(): void {
    this.selectedClient = null;
  }

  updateClient(): void {
    if (!this.selectedClient) return;
    this.clientService.updateClient(this.selectedClient.id!, this.selectedClient).subscribe({
      next: (updated) => {
        // const idx = this.clients.findIndex(c => c.id === updated.id);
        // if (idx > -1) this.clients[idx] = updated;
        // this.fetchClients();
        this.closeModal();
        // window.location.reload();
        this.fetchClients();
      },
      error: (err) => {
        console.error('Error updating client', err);
      }
    });
  }
}
