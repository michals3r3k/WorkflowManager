import { Injectable, Inject } from '@angular/core';
import { LoginComponent } from '../../login/login.component';
import { MatDialog } from '@angular/material/dialog';
import { ResultToasterService } from '../result-toaster/result-toaster.service';

@Injectable({
  providedIn: 'root'
})
export class LoginDialogOpenerService {
  constructor(private dialog: MatDialog,
    private resultToaster: ResultToasterService
  ) { 
    // itentionally empty
  }

  open(showLogin: boolean) {
    const dialogRef = this.dialog.open(LoginComponent, {
      panelClass: "login-dialog",
      data: {isSignDivVisiable: !showLogin}
    });
    const loginComponent = dialogRef.componentInstance;
    loginComponent.afterLogin.subscribe(res => {
      if(res.success) {
        this.resultToaster.success("Login success");
        location.reload();
      }
      else {
        this.resultToaster.error(res.errors);
      }
    });
    loginComponent.afterRegister.subscribe(res => {
      if(res.success) {
        loginComponent.openLogin();
        this.resultToaster.success("Register success");
      }
      else {
        this.resultToaster.error(res.errors);
      }
    })
    return dialogRef;
  }
}
