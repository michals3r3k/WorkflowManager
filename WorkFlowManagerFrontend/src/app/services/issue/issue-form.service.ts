import { Injectable } from '@angular/core';
import { IssueFieldEditRest } from '../../orders-list/issue-field/issue-field.component';
import { HttpRequestService } from '../http/http-request.service';
import { Observable } from 'rxjs';
import { ServiceResult } from '../utils/service-result';

@Injectable({
  providedIn: 'root'
})
export class IssueFormService {

  constructor(private http: HttpRequestService) { }

  getForm(sourceOrganizationId: number, destinationOrganizationId: number): Observable<IssueFormRest> {
    return this.http.getGeneric<IssueFormRest>(`api/organization/${sourceOrganizationId}/issue-form/${destinationOrganizationId}`);
  }

  sendForm(sourceOrganizationId: number, form: IssueFormRest): Observable<ServiceResult> {
    return this.http.postGeneric<ServiceResult>(`api/organization/${sourceOrganizationId}/issue-form/send`, form);
  }

}

// TODO - do obsłużenia id, description, category i status
export interface IssueFormRest {
  title: string | null,
  id: string | null,
  description: string | null,
  category: string | null,
  status: string | null,
  fields: IssueFieldEditRest[]
}
