import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { OrderCreateComponent } from './order-create/order-create.component';
import { ClientOrderDialogComponent } from './client-order-dialog/client-order-dialog.component';
import { IssueDetailsRest, IssueDetailsService } from '../services/issue/issue-details.service';

@Component({
  selector: 'app-orders',
  templateUrl: './orders-list.component.html',
  styleUrls: ['./orders-list.component.css']
})
export class OrdersListComponent implements OnInit {
  @Input() organizationId: number;
  myIssues: IssueDetailsRest[];
  clientsIssues: IssueDetailsRest[];

  constructor(private dialog: MatDialog,
    private issueDetailsService: IssueDetailsService) { }

  ngOnInit() {
    this._loadIssues();
  }

  _loadIssues() {
    this.issueDetailsService.getOrganizationIssues(this.organizationId).subscribe(issues => {
      this.myIssues = issues.filter(issue => !issue.fromClient);
      this.clientsIssues = issues.filter(issue => issue.fromClient);
    });
  }

  openAddOrderDialog() {
    const dialogRef = this.dialog.open(OrderCreateComponent, {
      data: {organizationId: this.organizationId}
    });
    dialogRef.componentInstance.afterSendReport.subscribe(() => {
      this._loadIssues();
      dialogRef.close();
    });
  }

  openClientsIssueDetails(issue: IssueDetailsRest) {
    const issueId = issue.id;
    const dialogRef = this.dialog.open(ClientOrderDialogComponent, {data: {
      organizationId: this.organizationId,
      issueFieldsUrl: `api/organization/${this.organizationId}/client-issue/${issueId}`
    }});
    dialogRef.componentInstance.closeDialog.subscribe(() => {
      this._loadIssues();
      dialogRef.close();
    });
  }

  openMyIssueDetails(issue: IssueDetailsRest) {
    const issueId = issue.id;
    this.dialog.open(ClientOrderDialogComponent, {data: {
      issueFieldsUrl: `api/organization/${this.organizationId}/client-issue/${issueId}`
    }});
  }

}
