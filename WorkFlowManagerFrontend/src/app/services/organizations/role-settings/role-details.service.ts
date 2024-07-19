import { Injectable } from '@angular/core';
import { HttpRequestService } from '../../http/http-request.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RoleDetailsService {
  constructor(private httpService : HttpRequestService) { }

  public getRoleDetailsList(organizationId: number, role: string): Observable<RoleDetails> {
    return this.httpService.getGeneric<RoleDetails>(`api/organization/${organizationId}/role/${role}`);
  }

}

export interface RoleDetails {
  permissions: [{permission: string, selected: boolean}]
  members: [{userId: number, email: string, selected: boolean}]
}
