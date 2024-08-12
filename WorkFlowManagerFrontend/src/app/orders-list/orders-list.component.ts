import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { OrderCreateComponent } from './order-create/order-create.component';

@Component({
  selector: 'app-orders',
  templateUrl: './orders-list.component.html',
  styleUrls: ['./orders-list.component.css']
})
export class OrdersListComponent implements OnInit {
  @Input() organizationId: number;
  constructor(private dialog: MatDialog) { }

  ngOnInit() {
  }

  openAddOrderDialog() {
    const dialogRef = this.dialog.open(OrderCreateComponent, {
      data: {organizationId: this.organizationId}
    });
  }

}
