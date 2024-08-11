import { Component } from '@angular/core';

@Component({
  selector: 'app-issue-details',
  templateUrl: './issue-details.component.html',
  styleUrl: './issue-details.component.css'
})
export class IssueDetailsComponent {
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
