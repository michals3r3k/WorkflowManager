import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { HttpRequestService } from '../../services/http/http-request.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { RoleDetails, RoleDetailsService } from '../../services/organizations/role-settings/role-details.service';

@Component({
  selector: 'app-role-settings',
  templateUrl: './role-settings.component.html',
  styleUrls: ['./role-settings.component.css']
})
export class RoleSettingsComponent {
  disableBtnEnable = true;
  disablePermissionSave = true;
  deleteBtnHover = false;
  timerID: any;
  searchUser = "";

  organizationId: number;
  role: string;
  roles$: Observable<RoleDetails>;

  @Output() onClose : EventEmitter<null> = new EventEmitter();

  constructor(private service: RoleDetailsService,
    @Inject(MAT_DIALOG_DATA) private data: {organizationId: number, role: string}
  ) {
    this.organizationId = data.organizationId;
    this.role = data.role;
    this._loadRoles();
  }

  _loadRoles() {
    this.roles$ = this.service.getRoleDetailsList(this.organizationId, this.role);
    this.roles$.subscribe(x => {
      debugger;
      console.log(x);
    })
  }

  close() {
    this.onClose.emit();
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

  enableSavePermission() {
    this.disablePermissionSave = false;
  }

}
