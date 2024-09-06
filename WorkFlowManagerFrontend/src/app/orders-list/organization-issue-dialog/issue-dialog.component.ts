import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { debounceTime, filter, mergeMap, Observable, of, startWith, switchMap, tap } from 'rxjs';
import { FormControl, FormGroup } from '@angular/forms';
import { ProjectCreateModel, ProjectRest, ProjectService } from '../../services/project/project.service';
import { IssueDetailsRest, IssueDetailsService } from '../../services/issue/issue-details.service';
import { IssueProjectConnectorComponent } from './issue-project-connector/issue-project-connector.component';

@Component({
  selector: 'app-issue-dialog',
  templateUrl: './issue-dialog.component.html',
  styleUrl: './issue-dialog.component.css'
})
export class IssueDialogComponent implements OnInit {
  @Output() projectChange = new EventEmitter<null>();
  // input params
  organizationId: number;
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
    @Inject(MAT_DIALOG_DATA) private data: {
      forClient: boolean,
      organizationId: number,
      issueId: number
    }) {
    this.organizationId = data.organizationId;
    this.issueId = data.issueId;
    this.forClient = data.forClient;
    this.project$ = of(null);
    this.formGroup = new FormGroup({});
  }

  ngOnInit(): void {
    this.issue$ = this.issueDetailsService.getDetails(this.organizationId, this.issueId, this.forClient).pipe(
      tap(issue => {
        this.project$ = this._getProject(issue.projectId);
      })
    );
  }

  onAddTaskClicked(taskTitle: string) {
    // this.http.postGeneric<{taskIdOrNull: number | null, success: boolean, errors: [string]}>(`api/organization/${this.organizationId}/project/${this.projectId}/task/column/add-task`, {title: taskTitle, taskColumnId: group.id}).subscribe(res => {
    //   this.serviceResultHelper.handleServiceResult(res as ServiceResult, "Task created succefully", "Errors occured");
    //   this.loadTasks();
    // });
  }

  switchToEditMode() {
    this.editMode = true;
  }

  saveChanges() {
    // SAVE TASK LOGIC
    this.editMode = false;
  }

  openProjectConnector(issue: IssueDetailsRest) {
    const dialogRef = this.dialog.open(IssueProjectConnectorComponent, {data: {
      organizationId: this.organizationId,
      issueId: issue.id
    }});
    this.project$ = dialogRef.afterClosed().pipe(
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
