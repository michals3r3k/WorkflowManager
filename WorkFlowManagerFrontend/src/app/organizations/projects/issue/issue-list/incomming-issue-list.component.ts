import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { IssueGroupRest, IssueRest, IssueService } from '../../../../services/issue/issue.service';
import { IssueDialogComponent } from '../../../../orders-list/organization-issue-dialog/issue-dialog.component';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-incomming-issue-list',
  templateUrl: './incomming-issue-list.component.html',
  styleUrl: './incomming-issue-list.component.css'
})
export class IncommingIssueListComponent implements OnInit {
  organizationId: number | null;
  projectId: number | null;

  issueGroups$: Observable<IssueGroupRest[]>;

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
    if(this.organizationId && this.projectId) {
      this.issueGroups$ = this.issueService.getProjectIncomingIssues(this.organizationId, this.projectId);
    }
  }

  openDialog(issue: IssueRest) {
    this.dialog.open(IssueDialogComponent, {
      data: {
        forClient: false,
        organizationId: this.organizationId,
        projectId: this.projectId,
        issueId: issue.id
      }
    });
  }

}
