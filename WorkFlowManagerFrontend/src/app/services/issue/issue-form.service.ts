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

  getForm(sourceOrganizationId: number, destinationOrganizationId: number): Observable<IssueFormRest> {
    return this.http.getGeneric<IssueFormRest>(`api/organization/${sourceOrganizationId}/issue-form/${destinationOrganizationId}`);
  }

  sendForm(sourceOrganizationId: number, form: IssueFormRest): Observable<ServiceResult> {
    return this.http.postGeneric<ServiceResult>(`api/organization/${sourceOrganizationId}/issue-form/send`, form).pipe(
      tap(res => {
        if(res.success) {
          this.issueChangeSubject.next();
        }
      }));
  }

  getIssueChangeEvent(): Observable<void>{
    return this.issueChangeSubject.asObservable();
  }

}

export interface IssueFormRest {
  title: string | null,
  fields: IssueFieldEditRest[]
}
