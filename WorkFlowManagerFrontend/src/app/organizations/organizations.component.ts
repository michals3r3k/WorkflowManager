import { Component, OnInit } from '@angular/core';
import { OrganizationCreateComponent } from '../organizations/organization-create/organization-create.component';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { ResultToasterService } from '../services/result-toaster/result-toaster.service';
import { HttpRequestService } from '../services/http/http-request.service';
import { filter, map, Observable } from 'rxjs';
import { PermissionChecker, PermissionService } from '../services/permission/permission.service';

@Component({
  selector: 'app-organizations',
  templateUrl: './organizations.component.html',
  styleUrl: './organizations.component.css'
})
export class OrganizationsComponent implements OnInit{

  constructor(private dialog: MatDialog, 
    private resultToaster: ResultToasterService,
    private http: HttpRequestService,
    private permissionService: PermissionService) {
    // itentionally empty
  }

  organizations$: Observable<OrganizationRest[]>;

  ngOnInit(): void {
    this.permissionService.getPermissionChecker().subscribe(permissionChecker => {
      this.permissionChecker = permissionChecker;
      this._loadOrganizations();
    });
  }

  permissionChecker?: PermissionChecker;

  _loadOrganizations() {
    this.http.get("api/organization/list");
    this.organizations$ = this.http.getGeneric<OrganizationRest[]>("api/organization/list")
      .pipe(map(organizations => organizations.filter(organization => this.permissionChecker?.hasPermission(organization.id, "ORGANIZATION_R"))));
  }

  openCreateDialog() {
    const dialogRef = this.dialog.open(OrganizationCreateComponent);
    const createComponent = dialogRef.componentInstance;
    createComponent.onSuccess.subscribe(() => {
      this._loadOrganizations();
      this.resultToaster.success("Organization created successfully");
      dialogRef.close();
    })
  }

}

interface OrganizationRest {
  name: string,
  id: number,
  description: string,
}
