import { Component, OnInit } from '@angular/core';
import { HttpRequestService } from '../services/http/http-request.service';
import { Observable } from 'rxjs';
import { ServiceResult } from '../services/utils/service-result';
import { OrganizationMemberInvitationStatus } from '../organizations/organization-details/organization-details.component';
import { ServiceResultHelper } from '../services/utils/service-result-helper';
import { InvitationRest, InvitationService } from '../services/invitation/invitation.service';

@Component({
  selector: 'app-invitations',
  templateUrl: './invitations.component.html',
  styleUrls: ['./invitations.component.css']
})
export class InvitationsComponent implements OnInit {

  invitations$: Observable<InvitationRest[]>;

  constructor(private invitationService: InvitationService,
    private serviceResultHelper: ServiceResultHelper
  ) { }

  ngOnInit() {
    this._loadInvitations();
  }
  
  _loadInvitations() {
    this.invitations$ = this.invitationService.getList();
  }

  accept(invitation: InvitationRest) {
    this._handleResult(this.invitationService.accept(invitation.organizationId));
  }

  reject(invitation: InvitationRest) {
    this._handleResult(this.invitationService.reject(invitation.organizationId));
  }

  _handleResult(resObservable: Observable<ServiceResult>) {
    resObservable.subscribe(res => {
      this.serviceResultHelper.handleServiceResult(res, "Success", "Errors occurred");
      if(res.success) {
        this._loadInvitations();
      }
    });
  }

}
