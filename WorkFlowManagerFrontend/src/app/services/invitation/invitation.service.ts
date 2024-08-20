import { Injectable } from '@angular/core';
import { Observable, Subject, tap } from 'rxjs';
import { HttpRequestService } from '../http/http-request.service';
import { OrganizationMemberInvitationStatus } from '../../organizations/organization-details/organization-details.component';
import { ServiceResult } from '../utils/service-result';

@Injectable({
  providedIn: 'root'
})
export class InvitationService {
  invitationsChangeSubject: Subject<void> = new Subject();

  constructor(private http: HttpRequestService) { }

  getList() {
    return this.http.getGeneric<InvitationRest[]>('api/invitation/list');
  }

  accept(organizationId: number) {
    return this._doAction({organizationId: organizationId, invitationStatus: OrganizationMemberInvitationStatus.ACCEPTED});
  }

  reject(organizationId: number) {
    return this._doAction({organizationId: organizationId, invitationStatus: OrganizationMemberInvitationStatus.REJECTED});
  }

  _doAction(req: {organizationId: number, invitationStatus: OrganizationMemberInvitationStatus}): Observable<ServiceResult> {
    return this.http.postGeneric<ServiceResult>("api/invitation/change-invitation-status", req).pipe(tap(res => {
      if(res.success) {
        this.invitationsChangeSubject.next();
      }
    }));
  }

  getInvitationsChangeEvent(): Observable<void> {
    return this.invitationsChangeSubject.asObservable();
  }

}

export interface InvitationRest {
  organizationId: number,
  organizationName: string,
  invitationTime: string,
}
