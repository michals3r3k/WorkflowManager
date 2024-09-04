import { Injectable } from '@angular/core';
import { HttpRequestService } from '../http/http-request.service';
import { BehaviorSubject, map, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  permissionChecker: BehaviorSubject<PermissionChecker> = new BehaviorSubject(new PermissionChecker({}));
  permissionChecker$: Observable<PermissionChecker> = this.permissionChecker.asObservable();

  constructor(private http: HttpRequestService) {}

  getPermissionChecker(): Observable<PermissionChecker> {
    return this.http.getGeneric<PermissionData>("api/permissions").pipe(map(data => new PermissionChecker(data)));
  }

}

interface PermissionData {
  [key: number]: [string]
}

export class PermissionChecker {
  data: PermissionData;

  constructor(data: PermissionData) {
    this.data = data;
  }

  hasPermission(organizationId: number | null, permission: string | null): boolean {
    if(!organizationId || !permission) {
      return false;
    }
    const permissions = this.data[organizationId] || [];
    return permissions.indexOf(permission) >= 0;
  }

}
