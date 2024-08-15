import { Injectable } from '@angular/core';
import { Observable, Subject, tap } from 'rxjs';
import { HttpRequestService } from '../http/http-request.service';
import { ServiceResult } from '../utils/service-result';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  
  private projectChangeSubject = new Subject<null>();

  constructor(private http: HttpRequestService) { }

  create(organizationId: number, model: ProjectCreateModel): Observable<ServiceResult> {
    return this.http.postGeneric<ServiceResult>(`api/organization/${organizationId}/project/create`, model).pipe(
      tap(res => {
        if(res.success) {
          this.projectChangeSubject.next(null);
        }
      })
    );
  }

  createWithIssue(organizationId: number, issueId: number, model: ProjectCreateModel): Observable<ServiceResult> {
    return this.http.postGeneric<ServiceResult>(`api/organization/${organizationId}/issue/${issueId}/to-new-project`, model).pipe(
      tap(res => {
        if(res.success) {
          this.projectChangeSubject.next(null);
        }
      })
    );
  }

  getAll(organizationId: number): Observable<ProjectRest[]> {
    return this.http.getGeneric<ProjectRest[]>(`api/organization/${organizationId}/projects`);
  }

  getOwned(organizationId: number): Observable<ProjectRest[]> {
    return this.http.getGeneric<ProjectRest[]>(`api/organization/${organizationId}/projects-owned`);
  }

  getProjectsChangeEvent(): Observable<null>{
    return this.projectChangeSubject.asObservable();
  }
}

export interface ProjectRest {
  projectId: number,
  organizationId: number,
  name: string,
  description: string,
  role: string
}

export class ProjectCreateModel  {
  name: string;
  description: string;

  constructor() {
    this.name = "";
    this.description = "";
  }
}
