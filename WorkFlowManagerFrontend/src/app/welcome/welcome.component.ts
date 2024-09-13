import { Component, OnInit } from '@angular/core';
import { LoginDialogOpenerService } from '../services/login/login-dialog-opener.service';
import { LoggedUser, LoggedUserService } from '../services/login/logged-user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

  btnClicked = false;
  loggedUser: LoggedUser | any;

  navOptions = [
    {
      label: "Features",
      routerLink: ""
    },
    {
      label: "About Us",
      routerLink: "/about-us"
    }
  ]

  constructor(private loginDialogOpener: LoginDialogOpenerService,
    private loggedUserService: LoggedUserService,
    public router: Router
  ) { }


  ngOnInit() {
    this.loggedUserService.user$.subscribe(loggedUser => {
      this.loggedUser = loggedUser;
    });
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

  goToProfile() {
    this.router.navigate((['/profile']));
  }

}
