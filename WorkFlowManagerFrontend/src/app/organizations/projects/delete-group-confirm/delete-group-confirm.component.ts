import { Component, Inject, OnInit } from '@angular/core';
import { TaskColumn } from '../project-details/project-details.component';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-delete-group-confirm',
  templateUrl: './delete-group-confirm.component.html',
  styleUrls: ['./delete-group-confirm.component.css']
})
export class DeleteGroupConfirmComponent implements OnInit {

  group: TaskColumn;

  constructor(
    @Inject(MAT_DIALOG_DATA) private data: {group: TaskColumn},
    private dialogRef: MatDialogRef<DeleteGroupConfirmComponent>,
    private dialog: MatDialog) {
    this.group = data.group;
  }

  ngOnInit() {
  }

  confirm(): void {
    this.dialogRef.close(true);
  }

  cancel(): void {
    this.dialogRef.close();
  }
}
