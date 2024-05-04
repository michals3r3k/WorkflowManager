import { Component } from '@angular/core';
import { OrganizationCreateComponent } from '../organizations/organization-create/organization-create.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-organizations',
  templateUrl: './organizations.component.html',
  styleUrl: './organizations.component.css'
})
export class OrganizationsComponent {
  constructor(private dialog: MatDialog) {
    // itentionally empty
  }

  openCreateDialog() {
    this.dialog.open(OrganizationCreateComponent);
  }

}
