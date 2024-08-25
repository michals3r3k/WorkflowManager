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

  getProjectIssues(organizationId: number, projectId: number): Observable<IssueRest[]> {
    return this.http.getGeneric<IssueRest[]>(`api/organization/${organizationId}/issues/project/${projectId}`);
  }

}

export interface IssueRest {
  id: number,
  title: string,
  organizationId: number,
  organizationName: string,
  projectName: string | null,
  fromClient: boolean,
  status: string,
  created: string
}
