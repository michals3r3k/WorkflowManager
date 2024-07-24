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
    return this.postGeneric<any>(endpoint, body);
  }

  postGeneric<T>(endpoint: string, body: any | null): Observable<T> {
    let headers: HttpHeaders | null = this.getHttpHeaders();
    if(!headers) {
      return this.navigateToLogin<T>();
    }
    return this.http.post<T>(`http://localhost:8080/${endpoint}`, body, {headers: headers});
  }

  get(endpoint: string, responseType?: string): Observable<any> {
    return this.getGeneric<any>(endpoint, responseType);
  }

  getGeneric<T>(endpoint: string, responseType?: string): Observable<T> {
    let headers: HttpHeaders | null = this.getHttpHeaders();
    if(!headers) {
      return this.navigateToLogin<T>();
    }
    return this.http.get<T>(`http://localhost:8080/${endpoint}`, {headers: headers});
  }

  getHttpHeaders(): HttpHeaders | null {
    let token = localStorage.getItem("WorkflowManagerToken");
    if(!token) {
      return null;
    }
    return new HttpHeaders({
      'Authorization': `Bearer ` + JSON.parse(token).token
    });
  }

  private navigateToLogin<T>(): Observable<T> {
    this.router.navigateByUrl("/home?showLogin=true");
    return new Observable(subscriber => {
      subscriber.error(new Error("No token available"));
    });
  }

}
