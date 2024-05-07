import { Injectable, Inject, Output, EventEmitter } from '@angular/core';
import { LoginComponent } from '../../login/login.component';
import { MatDialog } from '@angular/material/dialog';
import { ResultToasterService } from '../result-toaster/result-toaster.service';

@Injectable({
  providedIn: 'root'
})
export class LoginDialogOpenerService {
  constructor(private dialog: MatDialog,
    private resultToaster: ResultToasterService) { 
    // itentionally empty
  }

  @Output() afterLogin: EventEmitter<{res: any, dialogRef: any}> = new EventEmitter();
  @Output() afterRegister: EventEmitter<{res: any, dialogRef: any}> = new EventEmitter();

  open(showLogin: boolean) {
    const dialogRef = this.dialog.open(LoginComponent, {
      panelClass: "login-dialog",
      data: {isSignDivVisiable: !showLogin}
    });
    const loginComponent = dialogRef.componentInstance;
    loginComponent.afterLogin.subscribe(res => {
      this.afterLogin.emit({res: res, dialogRef: dialogRef})
    });
    loginComponent.afterRegister.subscribe(res => {
      this.afterRegister.emit({res: res, dialogRef: dialogRef})
    })
    return dialogRef;
  }
}
