import { Injectable } from '@angular/core';
import { HttpRequestService } from '../http/http-request.service';
import { Observable } from 'rxjs';
import { ServiceResult } from '../utils/service-result';

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  constructor(private http: HttpRequestService) { }

  createForColumn(organizationId: number, projectId: number, columnId: number, title: string): Observable<ServiceResult> {
    return this.http.postGeneric<ServiceResult>(`api/organization/${organizationId}/project/${projectId}/task/column/add-task`, {title: title, taskColumnId: columnId});
  }

  createForIssue(organizationId: number, projectId: number, issueId: number, title: string): Observable<ServiceResult> {
    return this.http.postGeneric<ServiceResult>(`api/organization/${organizationId}/project/${projectId}/task/issue/add-task`, {title: title, issueId: issueId});
  }

}
