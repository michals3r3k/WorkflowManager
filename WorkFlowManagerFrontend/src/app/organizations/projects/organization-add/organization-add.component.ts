import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import { debounceTime, map, Observable, startWith, switchMap } from 'rxjs';
import { ResultToasterService } from '../../../services/result-toaster/result-toaster.service';
import { HttpRequestService } from '../../../services/http/http-request.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-organization-add',
  templateUrl: './organization-add.component.html',
  styleUrl: './organization-add.component.css'
})
export class OrganizationAddComponent {
  projectId: number;
  organizationNameControl = new FormControl();
  organizationOptions$: Observable<any[]>;  

  @Output() onOrganizationSelected : EventEmitter<number> = new EventEmitter();

  constructor(private http: HttpRequestService,
    private resultToaster: ResultToasterService,
    @Inject(MAT_DIALOG_DATA) private data: {projectId: number}) {
    this.projectId = data.projectId;  
    this.organizationOptions$ = this.organizationNameControl.valueChanges
      .pipe(
        startWith(''),
        debounceTime(400),
        switchMap(() => this.loadOrganizations()));
  }

  inviteOrganization() {
    let organizationName = this.organizationNameControl.value;
    let sub = this.organizationOptions$
      .pipe(map(organization => organization
        .filter(organization => organization.name === organizationName)
        .map(organization => organization.id)))
      .subscribe(organizationIds => {
        sub.unsubscribe();
        let organizationId : number | null = organizationIds[0] || null;
        if(!organizationId) {
          this.resultToaster.error("Organization isn't in the list.");
          return;
        }
        this.onOrganizationSelected.emit(organizationId);
      });
  }

  private loadOrganizations(): Observable<any[]> {
    return this.http.get("api/organizations/like/" + this.projectId + "/" + (this.organizationNameControl.value || null));
  }  

}
