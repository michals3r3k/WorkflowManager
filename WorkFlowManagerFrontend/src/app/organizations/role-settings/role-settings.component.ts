import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { RoleDetails, RoleDetailsService } from '../../services/organizations/role-settings/role-details.service';
import { ServiceResultHelper } from '../../services/utils/service-result-helper';

@Component({
  selector: 'app-role-settings',
  templateUrl: './role-settings.component.html',
  styleUrls: ['./role-settings.component.css']
})
export class RoleSettingsComponent {
  disableBtnEnable = true;
  deleteBtnHover = false;
  timerID: any;
  searchUser = "";

  organizationId: number;
  role: string;
  roleDetails$: Observable<RoleDetails>;

  @Output() onClose : EventEmitter<null> = new EventEmitter();
  @Output() onDelete : EventEmitter<null> = new EventEmitter();

  constructor(private service: RoleDetailsService,
    private serviceResultHelper: ServiceResultHelper,
    @Inject(MAT_DIALOG_DATA) private data: {organizationId: number, role: string}
  ) {
    this.organizationId = data.organizationId;
    this.role = data.role;
    this._getRoleDetails();
  }

  _getRoleDetails() {
    this.roleDetails$ = this.service.getRoleDetails(this.organizationId, this.role);
  }

  close() {
    this.onClose.emit();
  }

  delete() {
    this.service.deleteRole(this.organizationId, this.role).subscribe(result => {
      this.serviceResultHelper.handleServiceResult(result, "Role deleted successfully", "Errors occured");
      if(result.success) {
        this.close();
        this.onDelete.emit();
      }
    });
  }

  startDeleteBtnTimer() {
    this.deleteBtnHover = true;
    this.timerID = setTimeout(() => {
      this.disableBtnEnable = false;
    }, 2400);
  }

  resetDeleteBtnTimer() {
    this.deleteBtnHover = false;
    clearTimeout(this.timerID);
    this.disableBtnEnable = true;
  }

  save(roleDetails: RoleDetails) {
    this.service.setRoleDetails(this.organizationId, this.role, roleDetails).subscribe(result => {
      this.serviceResultHelper.handleServiceResult(result, "Role saved successfully", "Errors occured");
      if(result.success) {
        this._getRoleDetails();
      }
    });
  }

}
