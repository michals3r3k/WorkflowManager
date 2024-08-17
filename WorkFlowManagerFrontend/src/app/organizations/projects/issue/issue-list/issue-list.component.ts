import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { IssueDetailsComponent } from '../issue-details/issue-details.component';

@Component({
  selector: 'app-issue-list',
  templateUrl: './issue-list.component.html',
  styleUrl: './issue-list.component.css'
})
export class IssueListComponent {
  constructor(private dialog: MatDialog) {

  }

  openDialog() {
    const dialogRef = this.dialog.open(IssueDetailsComponent, {
      data: {}
    });
  }

}
