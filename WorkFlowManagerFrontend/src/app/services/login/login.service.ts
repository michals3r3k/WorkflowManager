import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  constructor(private http: HttpClient) { 
    // itentionally empty
  }

  login(email: string, password: string) {
    let body = {
      email: email, 
      password: password
    };
    this.http.post("http://localhost:8080/api/login", body)
      .subscribe(data => console.log(data));
    // TODO: store token in cookies / localstore
  }
}
