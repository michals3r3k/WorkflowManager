import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-menu-item',
  templateUrl: './menu-item.component.html',
  styleUrl: './menu-item.component.css'
})
export class MenuItemComponent {
  @Input() menuItem: MenuItemDto; 

}

export interface MenuItemDto {
  icon: string;
  label: string;
  showLabel: boolean;
}
