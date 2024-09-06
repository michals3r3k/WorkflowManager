import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { debounceTime, filter, map, mergeMap, Observable, of, startWith, switchMap, tap } from 'rxjs';
import { FormControl, FormGroup } from '@angular/forms';
import { ProjectCreateModel, ProjectRest, ProjectService } from '../../services/project/project.service';
import { IssueDetailsRest, IssueDetailsService } from '../../services/issue/issue-details.service';
import { IssueProjectConnectorComponent } from './issue-project-connector/issue-project-connector.component';
import { IssueService } from '../../services/issue/issue.service';
import { IssueFormService } from '../../services/issue/issue-form.service';
import { ServiceResultHelper } from '../../services/utils/service-result-helper';
import { HttpRequestService } from '../../services/http/http-request.service';
import { ServiceResult } from '../../services/utils/service-result';
import { TaskService } from '../../services/task/task.service';

@Component({
  selector: 'app-issue-dialog',
  templateUrl: './issue-dialog.component.html',
  styleUrl: './issue-dialog.component.css'
})
export class IssueDialogComponent implements OnInit {
  @Output() projectChange = new EventEmitter<null>();
  // input params
  organizationId: number;
  projectId: number | null;
  issueId: number;
  forClient: boolean;

  // view variables
  issue$: Observable<IssueDetailsRest>;
  project$: Observable<ProjectRest | null>;

  formGroup :FormGroup | undefined = undefined;
  editMode: boolean = false;
  
  constructor(
    private projectService: ProjectService,
    private issueDetailsService: IssueDetailsService,
    private dialog: MatDialog,
    private issueFormService: IssueFormService,
    private serviceResultHelper: ServiceResultHelper,
    private http: HttpRequestService,
    private taskService: TaskService,
    @Inject(MAT_DIALOG_DATA) private data: {
      forClient: boolean,
      organizationId: number,
      projectId?: number,
      issueId: number
    }) {
    this.organizationId = data.organizationId;
    this.projectId = data.projectId || null;
    this.issueId = data.issueId;
    this.forClient = data.forClient;
    this.project$ = of(null);
    this.formGroup = new FormGroup({});
  }

  ngOnInit(): void {
    this.issue$ = this.issueDetailsService.getDetails(this.organizationId, this.issueId, this.forClient).pipe(
      tap(issue => {
        this.projectId = issue.projectId;
        this.project$ = this._getProject(issue.projectId);
      })
    );
  }

  onAddTaskClicked(taskTitle: string, projectId: number) {
    if(!taskTitle) {
      return;
    }
    this.taskService.createForIssue(this.organizationId, projectId, this.issueId, taskTitle).subscribe(res => {
      this.serviceResultHelper.handleServiceResult(res, "Task created succefully", "Errors occured");
    });
  }

  switchToEditMode() {
    this.editMode = true;
  }

  saveChanges(issue: IssueDetailsRest) {
    this.issueFormService.editAsOrganization(this.organizationId, issue.form).subscribe(res => {
      this.serviceResultHelper.handleServiceResult(res, "Issue saved successfully", "Errors occured");
      if(res.success) {
        this.editMode = false;
      }
    });
  }

  openProjectConnector(issue: IssueDetailsRest) {
    const dialogRef = this.dialog.open(IssueProjectConnectorComponent, {data: {
      organizationId: this.organizationId,
      issueId: issue.id
    }});
    this.project$ = dialogRef.afterClosed().pipe(
      tap(projectId => this.projectId = projectId),
      filter(projectId => projectId),
      tap(() => this.projectChange.emit()),
      mergeMap(projectId => this._getProject(projectId)));
  }

  _getProject(projectId: number | null): Observable<ProjectRest | null> {
    if(projectId) {
      return this.projectService.getById(this.organizationId, projectId);
    }
    return of(null);
  }

}
