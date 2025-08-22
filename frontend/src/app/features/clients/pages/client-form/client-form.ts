import { Component } from '@angular/core';
import {CommonModule} from '@angular/common';

import {FormBuilder,FormGroup,ReactiveFormsModule,Validators} from '@angular/forms';
import {ClientService} from "../../services/client"
import {Client} from '../../models/client.model';
@Component({
  selector: 'app-client-form',
  imports: [CommonModule, ReactiveFormsModule,],
  templateUrl: './client-form.html',
  styleUrl: './client-form.css'
})
export class ClientForm {
clientForm : FormGroup

  constructor(private fb: FormBuilder,private clientService: ClientService) {
    this.clientForm = this.fb.group({
      name: ['', Validators.required],

    });
  }

  onSubmit() {
    if (this.clientForm.valid) {
      const clientData: Client = this.clientForm.value;

      // 👇 Use ClientService instead of HttpClient directly
      this.clientService.addClient(clientData).subscribe({
        next: (res:any) => {
          console.log('Saved Client:', res);
          alert('Client saved successfully!');
          this.clientForm.reset();
        },
        error: (err:any) => {
          console.error('Error saving client:', err);
          alert('Error while saving client');
        }
      });
    }
  }
}
