import { Component, EventEmitter, Inject, Input, Output } from '@angular/core';
import { HttpRequestService } from '../../../services/http/http-request.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-project-create',
  templateUrl: './project-create.component.html',
  styleUrl: './project-create.component.css'
})
export class ProjectCreateComponent {
  @Input() organizationId: string;
  
  projectCreateModel: ProjectAddModel = new ProjectAddModel();
  
  @Output() onSuccess: EventEmitter<any> = new EventEmitter(); 

  constructor(private httpService: HttpRequestService,
    @Inject(MAT_DIALOG_DATA) private data: {organizationId: string})
  {
    this.organizationId = data.organizationId;
  }

  create() {
    this.httpService.post("api/project/create/" + this.organizationId, this.projectCreateModel).subscribe(res => {
      if(res.success) {
        this.onSuccess.emit(res);
      }
      else {
        // TODO: resultToaster
        alert("error");
      }
    });
  }
}

class ProjectAddModel  {
  name: string;
  description: string;

  constructor() {
    this.name = "";
    this.description = "";
  }
}
