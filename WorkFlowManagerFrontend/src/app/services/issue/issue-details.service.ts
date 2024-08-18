import { Injectable } from '@angular/core';
import { HttpRequestService } from '../http/http-request.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class IssueDetailsService {

  constructor(private http: HttpRequestService) { }

  getOrganizationIssues(organizationId: number): Observable<IssueDetailsRest[]> {
    return this.http.getGeneric<IssueDetailsRest[]>(`api/organization/${organizationId}/issue-details`);
  }

  getProjectIssues(organizationId: number, projectId: number): Observable<IssueDetailsRest[]> {
    return this.http.getGeneric<IssueDetailsRest[]>(`api/organization/${organizationId}/issue-details/project/${projectId}`);
  }

}

export interface IssueDetailsRest {
  id: number,
  organizationId: number,
  organizationName: string,
  projectName: string | null,
  fromClient: boolean,
  status: string,
  created: string
}
