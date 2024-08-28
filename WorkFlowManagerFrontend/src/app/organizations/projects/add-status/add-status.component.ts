import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-add-status',
  templateUrl: './add-status.component.html',
  styleUrls: ['./add-status.component.css']
})
export class AddStatusComponent implements OnInit {

  status_name = "";

  constructor(@Inject(MAT_DIALOG_DATA) private data: {task: string},
  private dialogRef: MatDialogRef<AddStatusComponent>,
  private dialog: MatDialog) {
  }

  ngOnInit() {
  }

  confirm() {
    if (this.status_name.length === 0){
      return;
    }

    this.dialogRef.close(this.status_name);
  }

  cancel() {
    this.dialogRef.close(undefined);
  }
}
