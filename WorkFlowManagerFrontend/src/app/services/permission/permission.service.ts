import { Injectable } from '@angular/core';
import { HttpRequestService } from '../http/http-request.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PermissionService {

  constructor(private http: HttpRequestService) { }

  getPermissions(organizationIdOrNull: any): Observable<any> {
    return this.http.post("api/permissions", {organizationId: organizationIdOrNull});
  }
}
