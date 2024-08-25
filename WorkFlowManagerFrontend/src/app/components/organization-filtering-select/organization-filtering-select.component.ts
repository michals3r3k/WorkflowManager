import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import { debounceTime, Observable, startWith, switchMap, tap } from 'rxjs';
import { HttpRequestService } from '../../services/http/http-request.service';

@Component({
  selector: 'app-organization-filtering-select',
  templateUrl: './organization-filtering-select.component.html',
  styleUrl: './organization-filtering-select.component.css'
})
export class OrganizationFilteringSelectComponent {
  @Input() apiGetUrl: string;
  @Input() value: number | null;
  @Output() valueChange = new EventEmitter();
  organizationNameControl = new FormControl();
  organizationOptions$: Observable<any[]>;  
  
  constructor(private http: HttpRequestService) {
    this.organizationOptions$ = this.organizationNameControl.valueChanges
      .pipe(
        startWith(''),
        debounceTime(400),
        switchMap(() => this.loadOrganizations()),
        tap(organizations => {
          let organizationName = this.organizationNameControl.value;
          const organizationIds = organizations
            .filter(organization => organization.name === organizationName)
            .map(organization => organization.id);
          let organizationId: number | null = organizationIds[0] || null;
          this.valueChange.emit(organizationId);
        }));
  }

  private loadOrganizations(): Observable<any[]> {
    return this.http.get(`${this.apiGetUrl}/${(this.organizationNameControl.value || null)}`);
  } 

}
