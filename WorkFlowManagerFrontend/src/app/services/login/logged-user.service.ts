import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class LoggedUserService {
  constructor(private http: HttpClient) { 
    // itentionally empty
  }

  getLoggedUser(callback: (loggedUser?: LoggedUser) => void) {
    let token = localStorage.getItem("WorkflowManagerToken");
    if(!token) {
      return callback();
    }
    let userData = JSON.parse(token);
    this.http.post("http://localhost:8080/api/checkToken", userData)
      .subscribe(res => {
        if(res) {
          callback({email: userData.email});
        }
        else {
          callback();
        }
      });
  }

}

interface LoggedUser {
    email: string
}
