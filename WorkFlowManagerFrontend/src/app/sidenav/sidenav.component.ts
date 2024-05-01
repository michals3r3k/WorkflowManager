import { Component } from '@angular/core';
import { MenuItemDto } from './menu-item/menu-item.component';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.css'
})
export class SidenavComponent {
  opened: boolean = false;
  openMenuItem: MenuItemDto = {
    icon: "arrow_forward",
    label: "Show",
    showLabel: false
  };
  closeMenuItem: MenuItemDto = {
    icon: "close",
    label: "",
    showLabel: true
  };
  
  menuItems: MenuItemDto[] = [
    {
      icon: "home",
      label: "Home",
      showLabel: this.opened
    },
    {
      icon: "task",
      label: "Task",
      showLabel: this.opened
    },
    {
      icon: "calendar_month",
      label: "Calendar",
      showLabel: this.opened
    },
    {
      icon: "account_circle",
      label: "My Account",
      showLabel: this.opened
    },
    {
      icon: "login",
      label: "Login",
      showLabel: this.opened
    },
    {
      icon: "logout",
      label: "Logout",
      showLabel: this.opened
    },
  ];

  open() {
    this.opened = true;
    this._updateMenuItems();
  }

  close() {
    this.opened = false;
    this._updateMenuItems();
  }

  _updateMenuItems()
  {
    this.menuItems = this.menuItems.map(elt => <MenuItemDto>{icon: elt.icon, label: elt.label, showLabel: this.opened});
  }

}
