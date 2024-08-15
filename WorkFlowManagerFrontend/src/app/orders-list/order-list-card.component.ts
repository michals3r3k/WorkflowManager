import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-order-list-card',
  template: `
    <mat-card style="padding: 5px 10px; width: 280px; height: 45px;">
      <mat-card-title style="width: 250px; font-size: 1em; text-wrap: nowrap; overflow: hidden; text-overflow: ellipsis;">{{title}}</mat-card-title>
    </mat-card>
  `,
  styleUrls: ['./orders-list.component.css']
})
export class OrderListCardComponent {
  @Input() title: string;

}
