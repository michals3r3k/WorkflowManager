import { Component } from '@angular/core';
import { LoginDialogOpenerService } from '../services/login/login-dialog-opener.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  constructor(private loginDialogOpener: LoginDialogOpenerService) {
      // intentionally empty
  }

  openDialog() {
    this.loginDialogOpener.open(true);
  }
  
}
