import { Injectable } from '@angular/core';
import { HttpRequestService } from '../../http/http-request.service';
import { Observable } from 'rxjs';
import { ServiceResult } from '../../utils/service-result';

@Injectable({
  providedIn: 'root'
})
export class RoleDetailsService {
  constructor(private httpService : HttpRequestService) { }

  public getRoleDetails(organizationId: number, role: string): Observable<RoleDetails> {
    return this.httpService.getGeneric<RoleDetails>(`api/organization/${organizationId}/role/${role}`);
  }

  public setRoleDetails(organizationId: number, role: string, roleDetails: RoleDetails): Observable<ServiceResult> {
    return this.httpService.post(`api/organization/${organizationId}/role/${role}/edit`, roleDetails);
  }

  public deleteRole(organizationId: number, role: string): Observable<ServiceResult> {
    return this.httpService.get(`api/organization/${organizationId}/role/${role}/delete`);
  }

  public createRole(organizationId: number, role: string): Observable<ServiceResult> {
    return this.httpService.post(`api/organization/${organizationId}/role/create`, role);
  }

}

export interface RoleDetails {
  permissionSections: [{
    sectionName: string, 
    permissions: [{permission: string, selected: boolean}]
  }],
  members: [{userId: number, email: string, selected: boolean}]
}
