import { Component } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-organization-create',
  templateUrl: './organization-create.component.html',
  styleUrl: './organization-create.component.css'
})
export class OrganizationCreateComponent {
  organizationCreateModel: OrganizationAddModel = new OrganizationAddModel();

  constructor(private http: HttpClient) {
    // itentionally empty
  }

  create() {
    console.log(this.organizationCreateModel);
      let token = localStorage.getItem("WorkflowManagerToken");
      if(token) {
        let x = JSON.parse(token);
        const headers = new HttpHeaders({
          'Content-Type': 'application/json',
          'Authorization': `Bearer ` + x.token
        })
        this.http.post("http://localhost:8080/api/organization/create", this.organizationCreateModel, { headers: headers })
          .subscribe(res => {
            console.log(res);
          });
      }
  }
}

class OrganizationAddModel  {
  name: string;
  description: string;

  constructor() {
    this.name = "";
    this.description = "";
  }
}
