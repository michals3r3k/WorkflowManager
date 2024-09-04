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
import { OrderCreateComponent } from '../../orders-list/order-create/order-create.component';
import { ProjectCreateComponent } from '../projects/project-create/project-create.component';

@Component({
  selector: 'app-organization-details',
  templateUrl: './organization-details.component.html',
  styleUrls: ['./organization-details.component.css']
})
export class OrganizationDetailsComponent implements OnInit {
  organizationId: number | null;
  organization: any = null;
  
  members$: Observable<OrganizationMemberRest[]>;
  roles$: Observable<{name: string}[]>;
  
  searchUser: string = "";
  searchRole: string = ""; 

  projectR: boolean = false;
  projectC: boolean = false;
  memberR: boolean = false;
  memberU: boolean = false;
  roleR: boolean = false;
  roleU: boolean = false;
  orderR: boolean = false;
  orderU: boolean = false;
  orderSettingsU: boolean = false;

  constructor(private route: ActivatedRoute, private http: HttpRequestService,
    private serviceResultHelper: ServiceResultHelper,
    private dialog: MatDialog, 
    private permissionService: PermissionService) {
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get("id"); 
      this.organizationId = idParam == null ? null: +idParam;
      this.permissionService.getPermissionChecker().subscribe(permissionChecker => {
        this.projectR = permissionChecker.hasPermission(this.organizationId, "PROJECT_R");
        this.projectC = permissionChecker.hasPermission(this.organizationId, "PROJECT_C");
        this.memberR = permissionChecker.hasPermission(this.organizationId, "ORGANIZATION_MEMBER_R");
        this.memberU = permissionChecker.hasPermission(this.organizationId, "ORGANIZATION_MEMBER_U");
        this.roleR = permissionChecker.hasPermission(this.organizationId, "ROLE_R");
        this.roleU = permissionChecker.hasPermission(this.organizationId, "ROLE_U");
        this.orderR = permissionChecker.hasPermission(this.organizationId, "ORDER_R");
        this.orderU = permissionChecker.hasPermission(this.organizationId, "ORDER_U");
        this.orderSettingsU = permissionChecker.hasPermission(this.organizationId, "ORDER_SETTINGS_U");
        
        this.http.get("api/organization/" + this.organizationId).subscribe((res) => {
          this.organization = res;
        });
        this.loadMembers();
        this.loadRoles();
      });
    })
  }

  private loadMembers() {
    this.members$ = this.http.getGeneric<OrganizationMemberRest[]>(`api/organization/${this.organizationId}/member/list`);
  }

  private loadRoles() {
    this.roles$ = this.http.getGeneric<{name: string}[]>(`api/organization/${this.organizationId}/role/list`);
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

  deleteMember(member: OrganizationMemberRest) {
    this.http.postGeneric<ServiceResult>(`api/organization/${this.organizationId}/member/delete`, member.userId).subscribe(res => {
      this.serviceResultHelper.handleServiceResult(res, "Member deleted succesfully", "Errors occured");
      this.loadMembers();
    })
  }

  openProjectCreateDialog() {
    const dialogRef = this.dialog.open(ProjectCreateComponent, {
      data: {organizationId: this.organizationId}
    });
    dialogRef.componentInstance.onSuccess.subscribe(() => {
      dialogRef.close();
    })
  }

  openOrderCreateDialog() {
    const dialogRef = this.dialog.open(OrderCreateComponent, {
      data: {organizationId: this.organizationId}
    });
    dialogRef.componentInstance.afterSendReport.subscribe(() => {
      dialogRef.close();
    });
  }

}

export interface OrganizationMemberRest {
  userId: number,
  name: string,
  invitationStatus: OrganizationMemberInvitationStatus
}

export enum OrganizationMemberInvitationStatus {
  INVITED = 'INVITED',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED'
}
