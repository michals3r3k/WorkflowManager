import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HttpRequestService {
  constructor(private http: HttpClient, private router: Router, private route: ActivatedRoute) {
    // itentionally empty
  }

  post(endpoint: string, body: any | null): Observable<any> {
    let token = localStorage.getItem("WorkflowManagerToken");
    if(!token) {
      this.router.navigateByUrl("/home?showLogin=true");
      return new Observable(subscriber => {
        subscriber.error(new Error("No token available"));
      });
    }
    return this.http.post("http://localhost:8080/" + endpoint, body, { 
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ` + JSON.parse(token).token
      }) 
    });
  }

  get(endpoint: string): Observable<any> {
    let token = localStorage.getItem("WorkflowManagerToken");
    if(!token) {
      this.router.navigateByUrl("/home?showLogin=true");
      return new Observable(subscriber => {
        subscriber.error(new Error("No token available"));
      });
    }
    return this.http.get("http://localhost:8080/" + endpoint, { 
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ` + JSON.parse(token).token
      }) 
    });
  }

}
