import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-menu-item',
  templateUrl: './menu-item.component.html',
  styleUrl: './menu-item.component.css'
})
export class MenuItemComponent {
  @Input()
  matIcon: string;
  @Input()
  label: string;
  @Input()
  collapsed: boolean;

}
