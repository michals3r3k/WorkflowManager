import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { IssueFieldEditRest } from '../order-create/order-create.component';

@Component({
  selector: 'app-client-order-dialog',
  templateUrl: './client-order-dialog.component.html',
  styleUrl: './client-order-dialog.component.css'
})
export class ClientOrderDialogComponent {
  issueId: number;
  constructor(@Inject(MAT_DIALOG_DATA) private data: {issueId: number}) {
    this.issueId = data.issueId;
  }



}

interface IssueDetails {
  organizationName: string,
  fields: IssueFieldEditRest[],
}
