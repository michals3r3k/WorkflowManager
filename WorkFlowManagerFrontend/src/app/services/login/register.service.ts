import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(private http: HttpClient) {
    // itentionally empty
  }

  register(email: string, password: string, firstName: string, secondName: string, callback: (res: any) => void) {
    let body = {
      email: email, 
      password: password,
      firstName: firstName, 
      secondName: secondName
    };
    this.http.post("http://localhost:8080/api/register", body)
      .subscribe(data => callback(data));
  }
}
