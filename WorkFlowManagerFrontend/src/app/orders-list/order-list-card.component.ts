import { Component, Input } from '@angular/core';
import { IssueRest } from '../services/issue/issue.service';

@Component({
  selector: 'app-order-list-card',
  template: `
    <mat-card style="padding: 5px 10px; height: 100%; width: 100%;">
      <mat-card-title style="width: 250px; font-size: 1em; text-wrap: nowrap; overflow: hidden; text-overflow: ellipsis;">
        #{{issue.id}} {{issue.title}}
      </mat-card-title>
      <mat-card-subtitle>{{issue.organizationName}}</mat-card-subtitle>
      <mat-card-footer style="display: flex; justify-content: end;">
        <mat-chip-set>
          <mat-chip>{{issue.status}}</mat-chip>
          <mat-chip>{{issue.created}}</mat-chip>
        </mat-chip-set>
      </mat-card-footer>
    </mat-card>
  `,
  styleUrls: ['./orders-list.component.css']
})
export class OrderListCardComponent {
  @Input() issue: IssueRest;

}
