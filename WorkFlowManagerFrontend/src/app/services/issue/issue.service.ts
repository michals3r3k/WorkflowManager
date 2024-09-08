import { Injectable } from '@angular/core';
import { HttpRequestService } from '../http/http-request.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class IssueService {

  constructor(private http: HttpRequestService) { }

  getOrganizationIssues(organizationId: number): Observable<IssueRest[]> {
    return this.http.getGeneric<IssueRest[]>(`api/organization/${organizationId}/issues`);
  }

  getProjectIncomingIssues(organizationId: number, projectId: number): Observable<IssueGroupRest[]> {
    return this.http.getGeneric<IssueGroupRest[]>(`api/organization/${organizationId}/issues-incoming/project/${projectId}`);
  }

  getProjectOutgoingIssues(organizationId: number, projectId: number): Observable<IssueGroupRest> {
    return this.http.getGeneric<IssueGroupRest>(`api/organization/${organizationId}/issues-outgoing/project/${projectId}`);
  }

}

export interface IssueRest {
  id: number,
  title: string,
  organizationId: number,
  organizationName: string,
  projectName: string | null,
  forClient: boolean,
  status: string,
  category: string,
  created: string,
  hidden: boolean
}

export interface IssueGroupRest {
  organizationName: string,
  issueRestList: IssueRest[]
}
