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
  searchUser: string = "";
  organization: any = null;
  members$: Observable<any[] | null> = of(null);
  
  roles = [
    {
      name: "Admin",
      users: 2
    },
    {
      name: "PM",
      users: 3
    },
    {
      name: "Programist",
      users: 8
    },
    {
      name: "QA",
      role: 2
    }
  ];

  constructor(private route: ActivatedRoute, private http: HttpRequestService) {
    
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

  private loadMembers() {
    this.members$ = this.http.get("api/organization/" + this.organizationId + "/member/list");
  } 

}
