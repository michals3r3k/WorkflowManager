import { Component, EventEmitter, Output } from '@angular/core';
import { HttpRequestService } from '../../services/http/http-request.service';

@Component({
  selector: 'app-organization-create',
  templateUrl: './organization-create.component.html',
  styleUrl: './organization-create.component.css'
})
export class OrganizationCreateComponent {
  organizationCreateModel: OrganizationAddModel = new OrganizationAddModel();

  constructor(private httpService: HttpRequestService) {
    // itentionally empty
  }

  @Output() onSuccess: EventEmitter<any> = new EventEmitter(); 

  create() {
    this.httpService.post("api/organization/create", this.organizationCreateModel).subscribe(res => {
      if(res.success) {
        this.onSuccess.emit(res);
      }
      else {
        // TODO: resultToaster
        alert(res.errors);
      }
    });
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
