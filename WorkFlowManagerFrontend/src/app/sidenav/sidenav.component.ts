import { Component, OnInit } from '@angular/core';
import { LoggedUserService } from '../services/login/logged-user.service';
import { LoginService } from '../services/login/login.service';
import { LoginDialogOpenerService } from '../services/login/login-dialog-opener.service';
import { ResultToasterService } from '../services/result-toaster/result-toaster.service';
import { Router } from '@angular/router';

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
    private loginDialogOpener: LoginDialogOpenerService,
    private resultToaster: ResultToasterService,
    private router: Router
  ) {
    this.loginDialogOpener.afterLogin.subscribe(data => {
      if(data.res.success) {
        this.userLogged = true;
        this.resultToaster.success("Login success");  
        data.dialogRef.close();
      }
      else {
        this.resultToaster.error(data.res.errors);  
      }
    });
    this.loginDialogOpener.afterRegister.subscribe(data => {
      if(data.res.success) {
        this.resultToaster.success("Register success");  
        data.dialogRef.componentInstance.openLogin();
      }
      else {
        this.resultToaster.error(data.res.errors);  
      }
    });
    this.loginService.logoutSuccess.subscribe(() => {
      this.resultToaster.success("Logged out");
      this.router.navigate(["/home"]);
      this.userLogged = false;
    });
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
  }

  openLoginDialog() {
    this.loginDialogOpener.open(true);
  }

}
