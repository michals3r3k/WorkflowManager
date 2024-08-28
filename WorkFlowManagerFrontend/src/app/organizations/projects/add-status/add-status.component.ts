import { Component, Inject, OnInit, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { HttpRequestService } from '../../../services/http/http-request.service';
import { ServiceResult } from '../../../services/utils/service-result';
import { ServiceResultHelper } from '../../../services/utils/service-result-helper';

@Component({
  selector: 'app-add-status',
  templateUrl: './add-status.component.html',
  styleUrls: ['./add-status.component.css']
})
export class AddStatusComponent implements OnInit {
  status_name = "";
  organizationId: number;
  projectId: number;

  constructor(@Inject(MAT_DIALOG_DATA) data: {organizationId: number, projectId: number},
  private dialogRef: MatDialogRef<AddStatusComponent>,
  private serviceResultHelper: ServiceResultHelper,
  private http: HttpRequestService) {
    this.organizationId = data.organizationId;
    this.projectId = data.projectId;
  }

  ngOnInit() {
  }

  confirm() {
    if (this.status_name.length === 0){
      return;
    }
    this.http.postGeneric<ServiceResult>(`api/organization/${this.organizationId}/project/${this.projectId}/task/column/add`, this.status_name).subscribe(res => {
      this.serviceResultHelper.handleServiceResult(res, "Column added successfully", "Errors occured");
      if(res.success) {
        this.dialogRef.close(true);    
      }
    })
  }

  cancel() {
    this.dialogRef.close(false);
  }

}
