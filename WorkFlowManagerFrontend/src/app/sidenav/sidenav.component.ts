import { Component, OnInit } from '@angular/core';
import { LoggedUserService } from '../services/login/logged-user.service';
import { LoginService } from '../services/login/login.service';
import { LoginDialogOpenerService } from '../services/login/login-dialog-opener.service';
import { ResultToasterService } from '../services/result-toaster/result-toaster.service';
import { Router } from '@angular/router';
import { HttpRequestService } from '../services/http/http-request.service';
import { InvitationService } from '../services/invitation/invitation.service';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.css'
})
export class SidenavComponent implements OnInit {
  userLogged: boolean = false;
  opened: boolean = false;
  invitationsCount: number = 0;

  constructor(private loggedUserService: LoggedUserService, 
    private loginService: LoginService,
    private loginDialogOpener: LoginDialogOpenerService,
    private invitationService: InvitationService,
    private http: HttpRequestService,
    private resultToaster: ResultToasterService,
    private router: Router
  ) {
    this.loginDialogOpener.afterLogin.subscribe(data => {
      if(data.res.success) {
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
    });
  }

  _updateInvitationsCount() {
    if(!this.userLogged)
    {
      this.invitationsCount = 0;
      return;
    }
    this.http.getGeneric<number>("api/invitation/count").subscribe(invitationsCount => {
      this.invitationsCount = invitationsCount;
    });
  }

  ngOnInit() {
    this.loggedUserService.user$.subscribe(loggedUser => {
      this.userLogged = !!loggedUser;
      this._updateInvitationsCount();
    });
    this.invitationService.getInvitationsChangeEvent().subscribe(() => {
      this._updateInvitationsCount();
    })
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
