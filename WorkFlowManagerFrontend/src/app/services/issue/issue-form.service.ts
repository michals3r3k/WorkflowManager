import { Injectable } from '@angular/core';
import { IssueFieldEditRest } from '../../orders-list/issue-field/issue-field.component';
import { HttpRequestService } from '../http/http-request.service';
import { Observable, Subject, tap } from 'rxjs';
import { ServiceResult } from '../utils/service-result';

@Injectable({
  providedIn: 'root'
})
export class IssueFormService {
  private issueChangeSubject = new Subject<void>();

  constructor(private http: HttpRequestService) { }

  getForm(organizationId: number): Observable<IssueFormRest> {
    return this.http.getGeneric<IssueFormRest>(`api/issue-form/${organizationId}`);
  }

  createAsClient(sourceOrganizationId: number, form: IssueFormRest): Observable<ServiceResult> {
    return this.http.postGeneric<ServiceResult>(`api/issue-form/${sourceOrganizationId}/create`, form).pipe(
      tap(res => {
        if(res.success) {
          this.issueChangeSubject.next();
        }
      }));
  }

  editAsOrganization(organizationId: number, form: IssueFormRest): Observable<ServiceResult> {
    return this.http.postGeneric<ServiceResult>(`api/organization/${organizationId}/issue-form/edit`, form);
  }

  getIssueChangeEvent(): Observable<void>{
    return this.issueChangeSubject.asObservable();
  }

}

// TODO - do obsłużenia id, description, category i status
export interface IssueFormRest {
  issueId: number | null,
  title: string | null,
  description: string | null,
  category: string | null,
  status: string | null,
  fields: IssueFieldEditRest[],
  statusOptions: string[],
  categoryOptions: string[]
}
