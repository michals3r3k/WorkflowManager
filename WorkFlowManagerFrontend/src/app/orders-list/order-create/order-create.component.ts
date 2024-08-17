import { Component, EventEmitter, Inject, Input, Output } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { HttpRequestService } from '../../services/http/http-request.service';
import { ServiceResult } from '../../services/utils/service-result';
import { ServiceResultHelper } from '../../services/utils/service-result-helper';
import { IssueFieldEditRest } from '../issue-field/issue-field.component';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-order-create',
  templateUrl: './order-create.component.html',
  styleUrl: './order-create.component.css'
})
export class OrderCreateComponent {
  organizationId: number;
  pickedOrganizationId: number | null;
  @Output() afterSendReport = new EventEmitter();
  
  fields_column1: IssueFieldEditRest[];
  fields_column2: IssueFieldEditRest[];

  formGroup: FormGroup;

  constructor(private http: HttpRequestService, 
    @Inject(MAT_DIALOG_DATA) private data: {organizationId: number},
    private serviceResultHelper: ServiceResultHelper) {
    this.organizationId = data.organizationId;
  }

  loadDefinition() {
    if(!this.pickedOrganizationId) {
      this.fields_column1 = [];
      this.fields_column2 = [];
      return;
    }
    this.http.getGeneric<IssueFieldEditRest[]>(`api/organization/${this.pickedOrganizationId}/issue-template`).subscribe(fields => {
      this.fields_column1 = fields.filter(field => field.clientVisible && field.column == 1);
      this.fields_column2 = fields.filter(field => field.clientVisible && field.column == 2);
      const group: any = {};
      [...this.fields_column1, ...this.fields_column2].forEach(field => {
        group[field.key] = new FormControl(field.value === null ? '' : field.value, field.required ? [Validators.required] : []);
      });
      this.formGroup = new FormGroup(group);
    });
  }

  sendReport() {
    this.http.postGeneric<ServiceResult>(`api/organization/${this.organizationId}/issue-send-report`, [...this.fields_column1, ...this.fields_column2]).subscribe(result => {
      this.serviceResultHelper.handleServiceResult(result, "Raport has been send succefully", "Errors occured");
      if(result.success) {
        this.afterSendReport.emit();
      }
    })
  }

}
