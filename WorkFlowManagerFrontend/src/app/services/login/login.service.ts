import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  constructor(private http: HttpClient) { 
    // itentionally empty
  }

  login(email: string, password: string, callback: (res: LoginResponse) => void) {
    let body = {
      email: email, 
      password: password
    };
    this.http.post<LoginResponse>("http://localhost:8080/api/login", body)
      .subscribe(res => {
        console.log(res);
        if(res.success) {
          localStorage.setItem("WorkflowManagerToken", JSON.stringify({
            email: email,
            token: res.token
          }));
        }
        callback(res);
      });
  }

  logout() {
    localStorage.removeItem("WorkflowManagerToken");
  }

}

interface LoginResponse {
  token: string,
  success: boolean,
  errors: string[];
}
