import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { IssueRest } from '../../../../services/issue/issue.service';
import { IssueDetailsRest, IssueDetailsService } from '../../../../services/issue/issue-details.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-issue-details',
  templateUrl: './issue-details.component.html',
  styleUrl: './issue-details.component.css'
})
export class IssueDetailsComponent implements OnInit {
  organizationId: number;
  projectId: number;
  issueId: number;

  issue$: Observable<IssueDetailsRest>;

  constructor(private issueDetailsService: IssueDetailsService,
    @Inject(MAT_DIALOG_DATA) data: {
      organizationId: number,
      projectId: number,
      issueId: number
    }) {
    this.organizationId = data.organizationId;
    this.projectId = data.projectId;
    this.issueId = data.issueId;
  }
  ngOnInit(): void {
    this.issue$ = this.issueDetailsService.getDetails(this.organizationId, this.issueId, false);
  }
  
  tasks: any[] = [
    {
      name: "test",
      description: "test",
    }
  ];

  addTask() {
    this.tasks.push({
      name: "test",
      description: "test",
    });
  }

}
