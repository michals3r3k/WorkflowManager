import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { IssueDetailsRest } from '../../../../services/issue/issue-details.service';

@Component({
  selector: 'app-issue-details',
  templateUrl: './issue-details.component.html',
  styleUrl: './issue-details.component.css'
})
export class IssueDetailsComponent {
  issue: IssueDetailsRest;

  constructor(@Inject(MAT_DIALOG_DATA) data: {issue: IssueDetailsRest}) {
    this.issue = data.issue;
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
