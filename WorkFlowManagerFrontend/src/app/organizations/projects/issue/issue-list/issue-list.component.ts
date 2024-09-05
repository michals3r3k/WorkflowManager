import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { IssueDetailsComponent } from '../issue-details/issue-details.component';
import { ActivatedRoute } from '@angular/router';
import { IssueRest, IssueService } from '../../../../services/issue/issue.service';

@Component({
  selector: 'app-issue-list',
  templateUrl: './issue-list.component.html',
  styleUrl: './issue-list.component.css'
})
export class IssueListComponent implements OnInit {
  organizationId: number | null;
  projectId: number | null;

  issueGroups: IssueGroup[];

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
      this.issueService.getProjectIssues(this.organizationId, this.projectId).subscribe(issues => {
        const groups: any = {};
        for(let i = 0; i < issues.length; ++i) {
          const issue: IssueRest = issues[i];
          let group = groups[issue.organizationId]
          if(!group) {
            groups[issue.organizationId] = {
              organizationId: issue.organizationId, 
              organizationName: issue.organizationName, 
              issues: [issue]
            };
          }
          else {
            group.issues.push(issue);
          }
        }
        const issueGroups: IssueGroup[] = [];
        for (const group of Object.values(groups)) {
          issueGroups.push(group as IssueGroup);
        }
        this.issueGroups = issueGroups;
      });
    }
  }

  openDialog(issue: IssueRest) {
    const dialogRef = this.dialog.open(IssueDetailsComponent, {
      data: {
        organizationId: this.organizationId,
        projectId: this.projectId,
        issueId: issue.id
      }
    });
  }

}

interface IssueGroup {
  organizationId: number,
  organizationName: string,
  issues: IssueRest[];
}
