import { Component, EventEmitter, Inject, Input, OnInit, Output } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ServiceResultHelper } from '../../services/utils/service-result-helper';
import { FormGroup } from '@angular/forms';
import { IssueFormRest, IssueFormService } from '../../services/issue/issue-form.service';

@Component({
  selector: 'app-order-create',
  templateUrl: './order-create.component.html',
  styleUrl: './order-create.component.css'
})
export class OrderCreateComponent {
  sourceOrganizationId: number;
  destinationOrganizationId: number | null;
  @Output() afterSendReport = new EventEmitter();

  form: IssueFormRest | null;
  formGroup: FormGroup | null;

  constructor(private issueFormService: IssueFormService, 
    @Inject(MAT_DIALOG_DATA) private data: {organizationId: number},
    private serviceResultHelper: ServiceResultHelper) {
    this.sourceOrganizationId = data.organizationId;
    this.loadDefinition();
  }

  loadDefinition() {
    if(!this.destinationOrganizationId) {
      this.form = null;
      this.formGroup = null;
      return;
    }
    this.issueFormService.getForm(this.destinationOrganizationId).subscribe(form => {
      this.form = form;
      this.formGroup = new FormGroup({});
    });
  }

  sendReport() {
    if(this.form) {
      this.issueFormService.createAsClient(this.sourceOrganizationId, this.form).subscribe(result => {
        this.serviceResultHelper.handleServiceResult(result, "Raport has been send succefully", "Errors occured");
        if(result.success) {
          this.afterSendReport.emit();
        }
      });
    }
  }

}
