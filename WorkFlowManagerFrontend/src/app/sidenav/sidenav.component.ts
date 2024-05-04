import { Component, OnInit } from '@angular/core';
import { LoggedUserService } from '../services/login/logged-user.service';
import { LoginService } from '../services/login/login.service';
import { LoginDialogOpenerService } from '../services/login/login-dialog-opener.service';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.css'
})
export class SidenavComponent implements OnInit {
  userLogged: boolean = false;
  opened: boolean = false;

  constructor(private loggedUserService: LoggedUserService, 
    private loginService: LoginService,
    private loginDialogOpener: LoginDialogOpenerService) {
    // itentionally empty
  }

  ngOnInit() {
    this.loggedUserService.getLoggedUser(loggedUser => {
      this.userLogged = !!loggedUser;
    });
  }

  open() {
    this.opened = true;
  }

  close() {
    this.opened = false;
  }

  logout() {
    this.loginService.logout();
    location.reload();
  }

  openLoginDialog() {
    this.loginDialogOpener.open(true);
  }

}
