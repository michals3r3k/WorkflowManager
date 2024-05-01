import { Component, OnInit } from '@angular/core';

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

  constructor() { }

  ngOnInit() {
  }

  goClicked(): void{
    this.btnClicked = true;
    setTimeout(() => {
      this.btnClicked = false;
    }, 600);
  }

}
