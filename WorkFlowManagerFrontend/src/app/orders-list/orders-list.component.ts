import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { IssueDialogComponent } from './organization-issue-dialog/issue-dialog.component';
import { IssueRest, IssueService } from '../services/issue/issue.service';
import { IssueFormService } from '../services/issue/issue-form.service';

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
    private issueService: IssueService,
    private issueFormService: IssueFormService) { }

  ngOnInit() {
    this._loadIssues();
    this.issueFormService.getIssueChangeEvent().subscribe(() => {
      this._loadIssues();
    });
  }

  _loadIssues() {
    this.issueService.getOrganizationIssues(this.organizationId).subscribe(issues => {
      this.myIssues = issues.filter(issue => issue.forClient);
      this.clientsIssues = issues.filter(issue => !issue.forClient);
    });
  }

  openClientsIssueDetails(issue: IssueRest) {
    const issueId = issue.id;
    const dialogRef = this.dialog.open(IssueDialogComponent, {data: {
      organizationId: this.organizationId,
      issueId: issueId,
      forClient: false
    }});
    dialogRef.componentInstance.projectChange.subscribe(() => {
      this._loadIssues();
    });
  }

  openMyIssueDetails(issue: IssueRest) {
    const issueId = issue.id;
    this.dialog.open(IssueDialogComponent, {data: {
      organizationId: this.organizationId,
      issueId: issueId,
      forClient: true
    }});
  }

}
