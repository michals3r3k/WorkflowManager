import { Injectable } from '@angular/core';
import { IssueFormRest } from './issue-form.service';
import { HttpRequestService } from '../http/http-request.service';

@Injectable({
  providedIn: 'root'
})
export class IssueDetailsService {

  constructor(private http: HttpRequestService) { }

  getDetails(organizationId: number, issueId: number) {
    return this.http.getGeneric<IssueDetailsRest>(`api/organization/${organizationId}/issue-details/${issueId}`);
  }

}

export interface IssueDetailsRest {
  id: number;
  title: string;
  sourceOrganizationName: number;
  destinationOrganizationName: number;
  projectName: string | null;
  sourceOrganizationId: number;
  destinationOrganizationId: number;
  form: IssueFormRest;
}

