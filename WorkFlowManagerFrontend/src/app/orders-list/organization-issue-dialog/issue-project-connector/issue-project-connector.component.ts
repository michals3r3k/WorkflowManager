import { Component, Inject, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { debounceTime, Observable, of, startWith, switchMap } from 'rxjs';
import { ProjectCreateModel, ProjectService } from '../../../services/project/project.service';
import { ServiceResultHelper } from '../../../services/utils/service-result-helper';
import { ServiceResult } from '../../../services/utils/service-result';
import { HttpRequestService } from '../../../services/http/http-request.service';

@Component({
  selector: 'app-issue-project-connector',
  templateUrl: './issue-project-connector.component.html',
  styleUrls: ['./issue-project-connector.component.css']
})
export class IssueProjectConnectorComponent implements OnInit {
 
  organizationId: number;
  issueId: number;

  createProjectName: string;
  createProjectDescription: string;

  projectNameControl = new FormControl();
  projectOptions: ProjectOptionRest[];
  projectOptions$: Observable<ProjectOptionRest[]>;
  selectedProj: ProjectOptionRest | null = null;

  constructor(private http: HttpRequestService, 
    private dialogRef: MatDialogRef<IssueProjectConnectorComponent>,
    @Inject(MAT_DIALOG_DATA) private data: {
      organizationId: number,
      issueId: number
    },
    private projectService: ProjectService,
    private serviceResultHelper: ServiceResultHelper,
  ) {
    this.organizationId = data.organizationId;
    this.issueId = data.issueId;

    this.projectService.getOwned(this.organizationId).subscribe(projects => {
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

  ngOnInit() {
  }

  connectExistingProject() {
    this.http.getGeneric<ServiceResult>(`api/organization/${this.organizationId}/issue/${this.issueId}/to-existing-project/${this.selectedProj?.id}`).subscribe(res => {
      this.serviceResultHelper.handleServiceResult(res, "Issue added to project successfully", "Errors occured");
      if(res.success) {
        this.dialogRef.close(this.selectedProj?.id);
      }
    });
  }

  connectNewProject() {
    const projectCreate = new ProjectCreateModel();
    projectCreate.name = this.createProjectName;
    projectCreate.description = this.createProjectDescription;

    this.projectService.createWithIssue(this.organizationId, this.issueId, projectCreate).subscribe(res => {
      this.serviceResultHelper.handleServiceResult(res, "Project created successfully", "Errors occured");
      if(res.success) {
        this.dialogRef.close(res.projectId)
      }
    });
  }

  onProjectInputChange() {
    const inputValue = this.projectNameControl.value;
    if (!this.selectedProj || this.selectedProj.name !== inputValue) {
      this.selectedProj = null;
    }
  }

  onProjectOptionSelected(event: MatAutocompleteSelectedEvent): void {
    const selectedProjectName = event.option.value;
    this.projectOptions$.subscribe(projects => {
      this.selectedProj = projects.find(proj => proj.name === selectedProjectName) || null;
    });
  }

  loadSearchProjects(): Observable<any[]> {
    const searchTerm = this.projectNameControl.value?.toLowerCase() || '';
    return this.projectOptions$.pipe(
      switchMap(projects => {
        const filteredProjects = projects.filter(proj =>
          proj.name.toLowerCase().includes(searchTerm)
        );
        return of(filteredProjects);
      })
    );
  }
}

interface ProjectOptionRest {
  id: number;
  name: string;
}