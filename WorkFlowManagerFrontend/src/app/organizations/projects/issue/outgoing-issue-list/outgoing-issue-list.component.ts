import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { IssueGroupRest, IssueRest, IssueService } from '../../../../services/issue/issue.service';
import { IssueDialogComponent } from '../../../../orders-list/organization-issue-dialog/issue-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-outgoing-issue-list',
  templateUrl: './outgoing-issue-list.component.html',
  styleUrl: './outgoing-issue-list.component.css'
})
export class OutgoingIssueListComponent {
  organizationId: number | null;
  projectId: number | null;

  group$: Observable<IssueGroupRest>;

  constructor(private dialog: MatDialog,
    private route: ActivatedRoute,
    private issueService: IssueService) { }

    ngOnInit() {
      this.route.paramMap.subscribe(params => {
        const projectId = params.get("projectId");
        const organizationId = params.get("organizationId");
        const taskId = params.get("taskId");
        this.projectId = projectId == null ? null : +projectId;
        this.organizationId = organizationId == null ? null : +organizationId;
        this._loadIssues();
      })    
    }

  _loadIssues() {
    console.log(this.organizationId?.toString() + ' | ' + this.projectId?.toString());
    if(this.organizationId && this.projectId) {
      this.group$ = this.issueService.getProjectOutgoingIssues(this.organizationId, this.projectId);
    }
  }

  openDialog(issue: IssueRest) {
    this.dialog.open(IssueDialogComponent, {
      data: {
        forClient: true,
        organizationId: issue.organizationId,
        projectId: this.projectId,
        issueId: issue.id
      }
    });
  }

}
