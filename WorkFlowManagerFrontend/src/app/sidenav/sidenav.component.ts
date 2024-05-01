import { Component } from '@angular/core';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.css'
})
export class SidenavComponent {
  opened: boolean = false;
  menuItems = [
    {
      icon: "home",
      label: "Home",
    },
    {
      icon: "task",
      label: "Task",
    },
    {
      icon: "calendar_month",
      label: "Calendar",
    },
    {
      icon: "account_circle",
      label: "My Account"
    },
    {
      icon: "login",
      label: "Login"
    },
    {
      icon: "logout",
      label: "Logout"
    },
  ];

  open() {
    this.opened = true;
  }

  close() {
    this.opened = false;
  }

}
