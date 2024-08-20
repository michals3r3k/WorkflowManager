import { Component, OnInit } from '@angular/core';
import { HttpRequestService } from '../../services/http/http-request.service';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { OrganizationMemberPickerComponent } from '../organization-member-picker/organization-member-picker.component';
import { RoleSettingsComponent } from '../role-settings/role-settings.component';
import { RoleCreateComponent } from '../role-create/role-create.component';
import { PermissionService } from '../../services/permission/permission.service';
import { ServiceResult } from '../../services/utils/service-result';
import { ServiceResultHelper } from '../../services/utils/service-result-helper';

@Component({
  selector: 'app-organization-details',
  templateUrl: './organization-details.component.html',
  styleUrls: ['./organization-details.component.css']
})
export class OrganizationDetailsComponent implements OnInit {
  organizationId: number | null;
  searchUser: string = "";
  organization: any = null;
  members$: Observable<OrganizationMemberRest[]>;
  roles$: Observable<any[] | null> = of(null);

  projectR: boolean = false;
  memberR: boolean = false;
  roleR: boolean = false;

  constructor(private route: ActivatedRoute, private http: HttpRequestService,
    private serviceResultHelper: ServiceResultHelper,
    private dialog: MatDialog, 
    private permissionService: PermissionService) {
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get("id");
      this.organizationId = idParam == null ? null: +idParam;
      this.permissionService.getPermissions(this.organizationId).subscribe(res => {
        let permissions = new Set(res);
        this.projectR = permissions.has("PROJECT_R");
        this.memberR = permissions.has("ORGANIZATION_MEMBER_R");
        this.roleR = permissions.has("ROLE_R");
      });
      this.http.get("api/organization/" + this.organizationId).subscribe((res) => {
        this.organization = res;
      })
      this.loadMembers();
      this.loadRoles();
    })
  }

  private loadMembers() {
    this.members$ = this.http.getGeneric<OrganizationMemberRest[]>(`api/organization/${this.organizationId}/member/list`);
  }

  private loadRoles() {
    this.roles$ = this.http.get(`api/organization/${this.organizationId}/role/list`);
  }

  openAddUserDialg() {
    let dialogRef = this.dialog.open(OrganizationMemberPickerComponent);
    dialogRef.componentInstance.onUserSelected.subscribe(userId => {
      this.http.postGeneric<ServiceResult>(`api/organization/${this.organizationId}/member/add`, userId).subscribe(res => {
        this.serviceResultHelper.handleServiceResult(res, "Member invited successfully", "Errors occured");
        if(res.success) {
          this.loadMembers();
          dialogRef.close();
        }
      })
    });
  }

  openAddRoleDialog() {
    let dialogRef = this.dialog.open(RoleCreateComponent, {data: { organizationId: this.organizationId }});
    dialogRef.componentInstance.onCancel.subscribe(() => {
      dialogRef.close();
    });
    dialogRef.componentInstance.onCreate.subscribe(role => {
      dialogRef.close();
      this.loadRoles();
      this.openRoleSettingsDialog(role);
    });
  }

  openRoleSettingsDialog(role: string) {
    let dialogRef = this.dialog.open(RoleSettingsComponent, {data: {
      organizationId: this.organizationId,
      role: role
    }});
    dialogRef.componentInstance.onDelete.subscribe(() => {
      this.loadRoles();
    });
    dialogRef.componentInstance.onClose.subscribe(() => {
      dialogRef.close();
    });
  }

}

export interface OrganizationMemberRest {
  name: string,
  invitationStatus: OrganizationMemberInvitationStatus
}

export enum OrganizationMemberInvitationStatus {
  INVITED,
  ACCEPTED,
  REJECTED
}
