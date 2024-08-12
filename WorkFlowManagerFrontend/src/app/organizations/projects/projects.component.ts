import { Component, Input, OnInit } from '@angular/core';
import { HttpRequestService } from '../../services/http/http-request.service';
import { filter, map, Observable } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { ProjectCreateComponent } from './project-create/project-create.component';
import { ResultToasterService } from '../../services/result-toaster/result-toaster.service';

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrl: './projects.component.css'
})
export class ProjectsComponent implements OnInit {
  @Input() organizationId: number;
  projectsOwning$: Observable<any[]>;
  projectsReporting$: Observable<any[]>;

  constructor(private dialog: MatDialog, private http: HttpRequestService,
    private resultToaster: ResultToasterService) {
    // itentionally empty
  }
  
  ngOnInit() {
    this._loadProjects();
  }

  private _loadProjects() {
    var projects$: Observable<any[]> = this.http.get("api/organization/" + this.organizationId + "/projects");
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
    const createComponent = dialogRef.componentInstance;
    createComponent.onSuccess.subscribe(() => {
      this._loadProjects();
      this.resultToaster.success("Project created successfully");
      dialogRef.close();
    })
  }

}
