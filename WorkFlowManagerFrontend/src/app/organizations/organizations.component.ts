import { Component, OnInit } from '@angular/core';
import { OrganizationCreateComponent } from '../organizations/organization-create/organization-create.component';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { ResultToasterService } from '../services/result-toaster/result-toaster.service';
import { HttpRequestService } from '../services/http/http-request.service';

@Component({
  selector: 'app-organizations',
  templateUrl: './organizations.component.html',
  styleUrl: './organizations.component.css'
})
export class OrganizationsComponent implements OnInit{
  constructor(private dialog: MatDialog, private resultToaster: ResultToasterService,
    private http: HttpRequestService) {
    // itentionally empty
  }

  organizations: any[];

  _loadOrganizations() {
    this.http.get("api/organization/list").subscribe((res) => {
      this.organizations = res;
    })
  }

  ngOnInit(): void {
    this._loadOrganizations();
  }

  openCreateDialog() {
    const dialogRef = this.dialog.open(OrganizationCreateComponent);
    const createComponent = dialogRef.componentInstance;
    createComponent.onSuccess.subscribe(res => {
      this._loadOrganizations();
      this.resultToaster.success("Organization created successfully");
      dialogRef.close();
    })
  }

}
