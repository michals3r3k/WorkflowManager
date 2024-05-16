import { Component, OnInit } from '@angular/core';
import { HttpRequestService } from '../../services/http/http-request.service';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import { ResultToasterService } from '../../services/result-toaster/result-toaster.service';
import { MatDialog } from '@angular/material/dialog';
import { OrganizationMemberPickerComponent } from '../organization-member-picker/organization-member-picker.component';
import { RoleSettingsComponent } from '../role-settings/role-settings.component';
import { OrganizationCreateComponent } from '../organization-create/organization-create.component';
import { RoleCreateComponent } from '../role-create/role-create.component';

@Component({
  selector: 'app-organization-details',
  templateUrl: './organization-details.component.html',
  styleUrls: ['./organization-details.component.css']
})
export class OrganizationDetailsComponent implements OnInit {
  private organizationId: string | null;
  searchUser: string = "";
  organization: any = null;
  members$: Observable<any[] | null> = of(null);

  roles = [
    {
      name: "Admin",
      users: 2
    },
    {
      name: "PM",
      users: 3
    },
    {
      name: "Programist",
      users: 8
    },
    {
      name: "QA",
      role: 2
    }
  ];

  constructor(private route: ActivatedRoute, private http: HttpRequestService,
    private dialog: MatDialog, private resultToaster: ResultToasterService) {
    
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.organizationId = params.get("id");
      this.http.get("api/organization/" + this.organizationId).subscribe((res) => {
        this.organization = res;
      })
      this.loadMembers();
    })    
  }

  private loadMembers() {
    this.members$ = this.http.get("api/organization/" + this.organizationId + "/member/list");
  } 

  openAddUserDialg() {
    let dialogRef = this.dialog.open(OrganizationMemberPickerComponent);
    dialogRef.componentInstance.onUserSelected.subscribe(userId => {
      this.http.post("api/organization/member/add", {
        organizationId: this.organizationId,
        userId: userId
      }).subscribe(res => {
        if(res.success) {
          this.resultToaster.success("Member added successfully");
          this.loadMembers();
        }
        else {
          this.resultToaster.success("Unknown error");
        }
        dialogRef.close();
      })
    });
  }

  openAddRoleDialog() {
    let dialogRef = this.dialog.open(RoleCreateComponent, {data: { organizationId: this.organizationId }});
    dialogRef.componentInstance.onCancel.subscribe(() => {
      dialogRef.close();
    });
    dialogRef.componentInstance.onCreate.subscribe(() => {
      dialogRef.close();
      this.openRoleSettingsDialog('test');
    });
  }

  openRoleSettingsDialog(param: any) {
    let dialogRef = this.dialog.open(RoleSettingsComponent);
    dialogRef.componentInstance.onClose.subscribe(() => {
      dialogRef.close();
    });
  }

}
