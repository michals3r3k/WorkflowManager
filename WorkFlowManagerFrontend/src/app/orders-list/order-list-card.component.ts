import { Component, Input } from '@angular/core';
import { IssueDetailsRest } from '../services/issue/issue-details.service';

@Component({
  selector: 'app-order-list-card',
  template: `
    <mat-card style="padding: 5px 10px; height: 100%; width: 100%;">
      <mat-card-title style="width: 250px; font-size: 1em; text-wrap: nowrap; overflow: hidden; text-overflow: ellipsis;">#{{issueDetails.id}} {{issueDetails.organizationName}}</mat-card-title>
      <mat-card-footer style="display: flex; justify-content: end;">
        <mat-chip-set>
          <mat-chip>{{issueDetails.status}}</mat-chip>
          <mat-chip>{{issueDetails.created}}</mat-chip>
        </mat-chip-set>
      </mat-card-footer>
    </mat-card>
  `,
  styleUrls: ['./orders-list.component.css']
})
export class OrderListCardComponent {
  @Input() issueDetails: IssueDetailsRest;

}
