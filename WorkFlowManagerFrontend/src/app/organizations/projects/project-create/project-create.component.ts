import { Component, EventEmitter, Inject, Input, Output } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ProjectCreateModel, ProjectService } from '../../../services/project/project.service';
import { ServiceResultHelper } from '../../../services/utils/service-result-helper';

@Component({
  selector: 'app-project-create',
  templateUrl: './project-create.component.html',
  styleUrl: './project-create.component.css'
})
export class ProjectCreateComponent {
  @Input() organizationId: number;
  
  projectCreateModel: ProjectCreateModel = new ProjectCreateModel();
  
  @Output() onSuccess: EventEmitter<null> = new EventEmitter(); 

  constructor(
    private projectService: ProjectService,
    private serviceResultHelper: ServiceResultHelper,
    @Inject(MAT_DIALOG_DATA) private data: {organizationId: number})
  {
    this.organizationId = data.organizationId;
  }

  create() {
    this.projectService.create(this.organizationId, this.projectCreateModel).subscribe(res => {
      this.serviceResultHelper.handleServiceResult(res, "Project created successfully", "Errors occured");
      if(res.success) {
        this.onSuccess.emit(null);
      }
    });
  }
}
