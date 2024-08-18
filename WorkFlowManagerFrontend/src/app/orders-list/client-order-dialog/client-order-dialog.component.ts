import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { IssueFieldEditRest } from '../issue-field/issue-field.component';
import { HttpRequestService } from '../../services/http/http-request.service';
import { debounceTime, Observable, of, startWith, switchMap } from 'rxjs';
import { FormControl } from '@angular/forms';
import { ServiceResult } from '../../services/utils/service-result';
import { ServiceResultHelper } from '../../services/utils/service-result-helper';
import { ProjectCreateModel, ProjectService } from '../../services/project/project.service';
import { ResultToasterService } from '../../services/result-toaster/result-toaster.service';

@Component({
  selector: 'app-client-order-dialog',
  templateUrl: './client-order-dialog.component.html',
  styleUrl: './client-order-dialog.component.css'
})
export class ClientOrderDialogComponent implements OnInit{
  organizationId: number | null;
  issueId: number;
  issueFieldsUrl: string;
  issue$: Observable<IssueFieldsRest>;

  projectNameControl: FormControl = new FormControl();
  projectOptions: ProjectOptionRest[];
  projectOptions$: Observable<ProjectOptionRest[]>;

  existingProject: boolean = true;
  projectCreateModel: ProjectCreateModel = new ProjectCreateModel();

  @Output() closeDialog = new EventEmitter<null>();
  
  constructor(private http: HttpRequestService, 
    private serviceResultHelper: ServiceResultHelper,
    private projectService: ProjectService,
    private resultToasterService: ResultToasterService,
    @Inject(MAT_DIALOG_DATA) private data: {
      organizationId?: number
      issueFieldsUrl: string
    }) {
    this.organizationId = data.organizationId || null;
    this.issueFieldsUrl = data.issueFieldsUrl;

    if(this.organizationId) { // if organizationId is passed, then show project form
      projectService.getOwned(this.organizationId).subscribe(projects => {
        this.projectOptions = projects.map(project => {
          return {
            id: project.projectId, 
            name: project.name
          }
        });
      });

      this.projectOptions$ = this.projectNameControl.valueChanges
      .pipe(
        startWith(''),
        debounceTime(400),
        switchMap(() => {
          const name = this.projectNameControl.value;
          return of(this.projectOptions.filter(option => option.name.indexOf(name) >= 0));
        })
      );
    }
  }

  ngOnInit(): void {
    this.issue$ = this.http.getGeneric<IssueFieldsRest>(this.issueFieldsUrl);
  }

  toExistingProject(issue: IssueFieldsRest) {
    const project = this.projectOptions.filter(project => project.name == this.projectNameControl.value)[0];
    if(!project) {
      this.resultToasterService.error("Project isn't in the list");
    }
    this.http.getGeneric<ServiceResult>(`api/organization/${this.organizationId}/issue/${issue.id}/to-existing-project/${project.id}`).subscribe(res => {
      this.serviceResultHelper.handleServiceResult(res, "Issue added to project successfully", "Errors occured");
      if(res.success) {
        this.close();
      }
    });
  }

  toNewProject(organizationId: number, issue: IssueFieldsRest) {
    this.projectService.createWithIssue(organizationId, issue.id, this.projectCreateModel).subscribe(res => {
      this.serviceResultHelper.handleServiceResult(res, "Project created successfully", "Errors occured");
      if(res.success) {
        this.close();
      }
    });
  }

  close() {
    this.closeDialog.emit();
  }

}

interface ProjectOptionRest {
  id: number;
  name: string;
}

interface IssueFieldsRest {
  id: number,
  organizationName: string,
  col1Fields: IssueFieldEditRest[],
  col2Fields: IssueFieldEditRest[]
}
