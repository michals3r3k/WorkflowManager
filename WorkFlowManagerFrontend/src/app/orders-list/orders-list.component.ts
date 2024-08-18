import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { OrderCreateComponent } from './order-create/order-create.component';
import { OrganizationIssueDialogComponent } from './organization-issue-dialog/organization-issue-dialog.component';
import { IssueRest, IssueService } from '../services/issue/issue.service';

@Component({
  selector: 'app-orders',
  templateUrl: './orders-list.component.html',
  styleUrls: ['./orders-list.component.css']
})
export class OrdersListComponent implements OnInit {
  @Input() organizationId: number;
  myIssues: IssueRest[];
  clientsIssues: IssueRest[];

  constructor(private dialog: MatDialog,
    private issueService: IssueService) { }

  ngOnInit() {
    this._loadIssues();
  }

  _loadIssues() {
    this.issueService.getOrganizationIssues(this.organizationId).subscribe(issues => {
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

  openClientsIssueDetails(issue: IssueRest) {
    const issueId = issue.id;
    const dialogRef = this.dialog.open(OrganizationIssueDialogComponent, {data: {
      organizationId: this.organizationId,
      issueId: issueId,
      readOnly: false
    }});
    dialogRef.componentInstance.closeDialog.subscribe(() => {
      this._loadIssues();
      dialogRef.close();
    });
  }

  openMyIssueDetails(issue: IssueRest) {
    const issueId = issue.id;
    this.dialog.open(OrganizationIssueDialogComponent, {data: {
      organizationId: this.organizationId,
      issueId: issueId,
      readOnly: true
    }});
  }

}
