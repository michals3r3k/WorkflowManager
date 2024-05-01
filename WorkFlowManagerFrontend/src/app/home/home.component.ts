import { Component } from '@angular/core';
import { LoginComponent } from '../login/login.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  
  constructor(private dialog: MatDialog) {
      // intentionally empty
  }

  openDialog() {
      const dialogRef = this.dialog.open(LoginComponent);
      dialogRef.afterClosed().subscribe(() => {
          console.log('The dialog was closed');
      });
  }
  
}
