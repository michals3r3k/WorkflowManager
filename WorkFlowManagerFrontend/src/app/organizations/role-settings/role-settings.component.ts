import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { HttpRequestService } from '../../services/http/http-request.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Observable } from 'rxjs';

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
  roles$: Observable<any>;


  @Output() onClose : EventEmitter<null> = new EventEmitter();

  constructor(private http: HttpRequestService,
    @Inject(MAT_DIALOG_DATA) private data: {organizationId: number, role: string}
  ) {
    this.organizationId = data.organizationId;
    this._loadRoles();
  }

  _loadRoles() {
    this.roles$ = this.http.get("api/organization/" + this.organizationId + "/role/list");
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
