import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { HttpRequestService } from '../../services/http/http-request.service';
import { filter, map, Observable, Subscription } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { ProjectCreateComponent } from './project-create/project-create.component';
import { ProjectRest, ProjectService } from '../../services/project/project.service';

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrl: './projects.component.css'
})
export class ProjectsComponent implements OnInit, OnDestroy {
  @Input() organizationId: number;
  projectsOwning$: Observable<ProjectRest[]>;
  projectsReporting$: Observable<ProjectRest[]>;

  private projectChangeSubscription: Subscription;

  constructor(private dialog: MatDialog, private http: HttpRequestService,
    private projectService: ProjectService) {
    this.projectChangeSubscription = projectService.getProjectsChangeEvent().subscribe(() => this._loadProjects());
  }
  
  ngOnInit() {
    this._loadProjects();
  }

  ngOnDestroy() {
    this.projectChangeSubscription.unsubscribe();
  }

  private _loadProjects() {
    var projects$ = this.projectService.getAll(this.organizationId);
    this.projectsOwning$ = projects$.pipe(
      map(projects => projects.filter(project => project.role === "OWNER"))
    );
    this.projectsReporting$ = projects$.pipe(
      map(projects => projects.filter(project => project.role === "REPORTER"))
    );
  }

  openCreateDialog() {
    const dialogRef = this.dialog.open(ProjectCreateComponent, {
      data: {organizationId: this.organizationId}
    });
    dialogRef.componentInstance.onSuccess.subscribe(() => {
      dialogRef.close();
    })
  }

}
