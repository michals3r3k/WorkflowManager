import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoggedUserService {
  private user: BehaviorSubject<LoggedUser | null>;
  public user$: Observable<LoggedUser | null>;

  constructor() { 
    let token = localStorage.getItem("WorkflowManagerToken");
    if(token) {
      this.user = new BehaviorSubject<LoggedUser | null>({email : JSON.parse(token).email});
    }
    else {
      this.user = new BehaviorSubject<LoggedUser | null>(null);
    }
    this.user$ = this.user.asObservable();
  }

  setUser(user: LoggedUser) {
    this.user.next(user);
  }

  clearUser() {
    this.user.next(null);
  }

}

interface LoggedUser {
    email: string
}
