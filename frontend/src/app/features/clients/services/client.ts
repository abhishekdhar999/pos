import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Client} from '../models/client.model';

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private apiURLCreateClient = "http://localhost:8080/pos/api/clients/create"
private apiURLGetClients = "http://localhost:8080/pos/api/clients"
  private apiURLUpdateClient ="http://localhost:8080/pos/api/clients"


  constructor(private http : HttpClient) {
  }

  // addClient(client: Client):Observable<Client>{
  //   return this.http.post<Client>(this.apiURLCreateClient,client)
  // }
  addClient(client: Client): Observable<Client> {
    return this.http.post<Client>(this.apiURLCreateClient, client, { withCredentials: true });
  }

  getClients():Observable<Client[]> {
    return this.http.get<Client[]>(this.apiURLGetClients,{});
  }

  getClientsPaginated(page: number, size: number): Observable<any> {
    return this.http.get(
      `${this.apiURLGetClients}?page=${page}&size=${size}`,
      { withCredentials: true }   // 👈 important
    );
  }

updateClient(id: number, client: Client): Observable<Client> {
  return this.http.put<Client>(`${this.apiURLUpdateClient}/${id}`, client,
    {withCredentials:true}
  );
}
}
