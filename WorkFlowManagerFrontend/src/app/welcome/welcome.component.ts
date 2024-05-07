import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { LoginComponent } from '../login/login.component';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

  btnClicked = false;

  navOptions = [
    {
      label: "Overview",
      routerLink: ""
    },
    {
      label: "Features",
      routerLink: ""
    },
    {
      label: "Examples",
      routerLink: ""
    },
    {
      label: "About Us",
      routerLink: ""
    }
  ]

  constructor(private dialog: MatDialog) { }

  ngOnInit() {
  }

  goClicked(): void{
    this.btnClicked = true;
    setTimeout(() => {
      this.btnClicked = false;
      const dialogRef = this.dialog.open(LoginComponent);
      dialogRef.afterClosed().subscribe(() => {
          console.log('The dialog was closed');
      });
    }, 600);
  }

}
