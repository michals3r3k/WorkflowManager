import { Injectable, Inject } from '@angular/core';
import { LoginComponent } from '../../login/login.component';
import { MatDialog } from '@angular/material/dialog';

@Injectable({
  providedIn: 'root'
})
export class LoginDialogOpenerService {
  constructor(private dialog: MatDialog) { 
    // itentionally empty
  }

  open(showLogin: boolean) {
    const dialogRef = this.dialog.open(LoginComponent, {
      panelClass: "login-dialog",
      data: {isSignDivVisiable: !showLogin}
    });
    dialogRef.componentInstance.afterLogin.subscribe(res => {
      if(res.success) {
        location.reload();
      }
      else {
        alert(res.errors);
      }
    });
    return dialogRef;
  }
}
