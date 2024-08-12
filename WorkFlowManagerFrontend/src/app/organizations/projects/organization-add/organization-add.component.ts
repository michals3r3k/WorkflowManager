import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { ResultToasterService } from '../../../services/result-toaster/result-toaster.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-organization-add',
  templateUrl: './organization-add.component.html',
  styleUrl: './organization-add.component.css'
})
export class OrganizationAddComponent {
  projectId: number;
  organizationId: number; // value set in organization selector

  @Output() onOrganizationSelected : EventEmitter<number> = new EventEmitter();

  constructor(private resultToaster: ResultToasterService,
    @Inject(MAT_DIALOG_DATA) data: {projectId: number}) {
    this.projectId = data.projectId;  
  }

  inviteOrganization() {
    if(!this.organizationId) {
      this.resultToaster.error("Organization isn't in the list.");
    }
    else {
      this.onOrganizationSelected.emit(this.organizationId);
    }
  } 

}
