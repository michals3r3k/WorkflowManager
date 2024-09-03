import { Injectable } from '@angular/core';
import { Stomp } from '@stomp/stompjs';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import SockJS from 'sockjs-client/dist/sockjs';
import { EventEmitter } from 'ws';
import { HttpRequestService } from '../http/http-request.service';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  stompClient: any;
  connected = false
  connectedSubject: BehaviorSubject<boolean> = new BehaviorSubject(false);

  constructor(private http: HttpRequestService) {}

  getConnectedObservable(): Observable<boolean> {
    return this.connectedSubject.asObservable();
  }

  subscribe<T>(url: string): Observable<T> {
    this._checkConnected();
    const subject = new Subject<T>();
    this.stompClient.subscribe(url, (message: any) => {
      subject.next(JSON.parse(message.body));
    });
    return subject.asObservable();
  }

  send(url: string, message: any): void {
    this._checkConnected();
    this.stompClient.send(url, {}, JSON.stringify(message));
  }

  _checkConnected() {
    if(!this.connected) {
      throw new Error("Websocket not connected");
    }
  }

  connect() {
    const token = this.http.getToken();
    if (token && !this.connected) {
      const socket = new SockJS('//localhost:8080/ws');

      // Inicjalizuj nowy klient STOMP
      this.stompClient = Stomp.over(socket);

      // Zaktualizuj funkcję fabryczną Websocket w klienta STOMP, aby używała sockjs-client
      this.stompClient.webSocketFactory = () => {
        return new SockJS('//localhost:8080/ws');
      };

      // Opcjonalnie możesz zarejestrować funkcję wywołania zwrotnego na zdarzenie połączenia
      this.stompClient.onConnect = (frame: any) => {
        console.log('Connected to websocket');
        this.connected = true
        this.connectedSubject.next(true);
      };

      // Zarejestruj funkcję wywołania zwrotnego na zdarzenie połączenia
      this.stompClient.connectHeaders = {
        Authorization: `Bearer ${token}`
      };
      this.stompClient.activate(); 
    } else {
      console.log('Already connected to websocket');
    }
  }

  disconnect() {
    if (this.connected && this.stompClient) {
      this.stompClient.deactivate();
      this.connected = false;
      this.connectedSubject.next(false);
      console.log('Disconnected from websocket');
    } else {
      console.log('Websocket is already disconnected');
    }
  }

}
