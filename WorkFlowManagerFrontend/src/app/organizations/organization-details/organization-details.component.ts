import { Component, OnInit } from '@angular/core';
import { HttpRequestService } from '../../services/http/http-request.service';
import { ActivatedRoute } from '@angular/router';
import { FormControl } from '@angular/forms';
import { Observable, debounceTime, filter, map, of, startWith, switchMap } from 'rxjs';
import { ResultToasterService } from '../../services/result-toaster/result-toaster.service';

@Component({
  selector: 'app-organization-details',
  templateUrl: './organization-details.component.html',
  styleUrls: ['./organization-details.component.css']
})
export class OrganizationDetailsComponent implements OnInit {
  private organizationId: string | null;
  userEmailControl = new FormControl();
  filteredOptions$: Observable<any[]>;
  searchUser: string = "";
  organization: any = null;
  members$: Observable<any[] | null> = of(null);

  constructor(private route: ActivatedRoute, private http: HttpRequestService,
    private resultToaster: ResultToasterService) {
    this.filteredOptions$ = this.userEmailControl.valueChanges
    .pipe(
      startWith(''),
      debounceTime(400),
      switchMap(val => this.filter()));
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.organizationId = params.get("id");
      this.http.get("api/organization/" + this.organizationId).subscribe((res) => {
        this.organization = res;
      })
      this.loadMembers();
    })    
  }

  addMember() {
    let email = this.userEmailControl.value;
    let subscription = this.filteredOptions$
      .pipe(
        map(list => list
          .filter(user => user.name === email)
          .map(user => user.id)),
        filter(list => list.length === 1))
      .subscribe(userIds => {
        subscription.unsubscribe();
        let userId = userIds[0] || null;
        if(!userId) {
          this.resultToaster.error("User isn't in the list.");
          return;
        }
        this.http.post("api/organization/member/add", {
          organizationId: this.organizationId,
          userId: userId
        }).subscribe(res => {
          if(res.success) {
            this.resultToaster.success("Member added successfully")
            this.loadMembers();
          }
        })
      })
  }

  private loadMembers() {
    this.members$ = this.http.get("api/organization/" + this.organizationId + "/member/list");
  }

  private filter(): Observable<any[]> {
    return this.http.get("api/users/like/" + (this.userEmailControl.value || null));
  }  

}
