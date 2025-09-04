import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClientService } from "../../services/client";
import { Client } from '../../models/client.model';
import { ToastService } from '../../../../components/errormodel/toast.service';

@Component({
  selector: 'app-client-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './client-form.html',
  styleUrl: './client-form.css'
})
export class ClientForm {
  clientForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private clientService: ClientService,
    private toastService: ToastService
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
        },
        error: (err: any) => {
          console.error('Error saving client:', err);
          const errorMessage = err.error?.message || 'Error saving client. Please try again.';
          this.toastService.error(errorMessage);
        }
      });
    }
  }
}
