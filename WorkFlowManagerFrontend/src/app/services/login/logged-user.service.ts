import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpRequestService } from '../http/http-request.service';

@Injectable({
  providedIn: 'root'
})
export class LoggedUserService {
  private user: BehaviorSubject<LoggedUser | null> = new BehaviorSubject<LoggedUser | null>(null);;
  public user$: Observable<LoggedUser | null> = this.user.asObservable();

  constructor(private http: HttpRequestService) { 
    const token = this.http.getToken();
    if(token) {
      this.updateUser();
    }
  }

  updateUser() {
    this.http.getGeneric<LoggedUser>("api/users/current-user").subscribe(user => {
      this.setUser(user);
    });
  }

  setUser(user: LoggedUser) {
    this.user.next(user);
  }

  clearUser() {
    this.user.next(null);
  }

}

export interface LoggedUser {
    id: number,
    email: string,
    name: string,
}
