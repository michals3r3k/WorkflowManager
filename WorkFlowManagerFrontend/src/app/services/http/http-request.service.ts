import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class HttpRequestService {
  constructor(private http: HttpClient, private router: Router, private route: ActivatedRoute) {
    // itentionally empty
  }

  post(endpoint: string, body: any | null, callback: (res: any) => void) {
    debugger;
    let token = localStorage.getItem("WorkflowManagerToken");
    if(!token) {
      this.router.navigate(["/home"]);
      return;
    }
    this.http.post("http://localhost:8080/" + endpoint, body, { 
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ` + JSON.parse(token).token
      }) 
    }).subscribe(res => {
      callback(res);
    });
  }

  get(endpoint: string, callback: (res: any) => void) {
    debugger;
    let token = localStorage.getItem("WorkflowManagerToken");
    if(!token) {
      this.router.navigate(["/home"]);
      return;
    }
    this.http.get("http://localhost:8080/" + endpoint, { 
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ` + JSON.parse(token).token
      }) 
    }).subscribe(res => {
      callback(res);
    });
  }

}
