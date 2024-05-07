import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ResultToasterComponent } from '../../result-toaster/result-toaster.component';

@Injectable({
  providedIn: 'root'
})
export class ResultToasterService {
  constructor(private snackBar: MatSnackBar) {
    // itentionally empty
   }

  success(message: string) {
    this._showMessage(message, 'success');
  }

  error(message: string) {
    this._showMessage(message, 'error');
  }

  private _showMessage(message: string, type: string) {
    this.snackBar.openFromComponent(ResultToasterComponent, {
      data: { message: message, type: type },
      duration: 2000,
      verticalPosition: 'top',
      horizontalPosition: 'end'
    });
  }
}
