import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Client} from '../models/client.model';

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private apiURLCreateClient = "http://localhost:8080/pos/api/clients"
private apiURLGetClients = "http://localhost:8080/pos/api/clients/all"
  private apiURLUpdateClient ="http://localhost:8080/pos/api/clients/update"


  constructor(private http : HttpClient) {
  }

  addClient(client: Client):Observable<Client>{
    return this.http.post<Client>(this.apiURLCreateClient,client)
  }

  getClients():Observable<Client[]> {
    return this.http.get<Client[]>(this.apiURLGetClients);
  }

updateClient(id: number, client: Client): Observable<Client> {
  return this.http.put<Client>(`${this.apiURLUpdateClient}/${id}`, client);
}
}
