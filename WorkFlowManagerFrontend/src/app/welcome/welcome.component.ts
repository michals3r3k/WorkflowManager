import { Component, OnInit } from '@angular/core';
import { LoginDialogOpenerService } from '../services/login/login-dialog-opener.service';

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

  constructor(private loginDialogOpener: LoginDialogOpenerService) {

  }

  ngOnInit() {
  }

  goClicked(): void{
    this.btnClicked = true;
    setTimeout(() => {
      this.btnClicked = false;
      const dialogRef = this.loginDialogOpener.open(true);
      dialogRef.afterClosed().subscribe(() => {
          console.log('The dialog was closed');
      });
    }, 600);
  }

}
