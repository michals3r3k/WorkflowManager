import { Component } from '@angular/core';
import {
  CdkDragDrop,
  CdkDrag,
  CdkDropList,
  CdkDropListGroup,
  moveItemInArray,
  transferArrayItem,
} from '@angular/cdk/drag-drop';
import { MatDialog } from '@angular/material/dialog';
import { ResultToasterService } from '../../../services/result-toaster/result-toaster.service';
import { HttpRequestService } from '../../../services/http/http-request.service';
import { OrganizationAddComponent } from '../organization-add/organization-add.component';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-project-details',
  templateUrl: './project-details.component.html',
  styleUrl: './project-details.component.css'
})
export class ProjectDetailsComponent {
  taskGroups: TaskGroup[] = [
    {
      groupName: "Group1",
      tasks: ['Get to work', 'Pick up groceries', 'Go home', 'Fall asleep']
    },
    {
      groupName: "Group2",
      tasks: ['Get up', 'Brush teeth', 'Take a shower', 'Check e-mail', 'Walk dog']
    },
    {
      groupName: "Group3",
      tasks: ['Get up', 'Brush teeth', 'Take a shower', 'Check e-mail', 'Walk dog']
    },
    {
      groupName: "Group4",
      tasks: ['Get up', 'Brush teeth', 'Take a shower', 'Check e-mail', 'Walk dog']
    },
    {
      groupName: "Group5",
      tasks: ['Get up', 'Brush teeth', 'Take a shower', 'Check e-mail', 'Walk dog']
    },
    {
      groupName: "Group6",
      tasks: ['Get up', 'Brush teeth', 'Take a shower', 'Check e-mail', 'Walk dog']
    },
    {
      groupName: "Group2",
      tasks: ['Get up', 'Brush teeth', 'Take a shower', 'Check e-mail', 'Walk dog']
    }
  ];

  projectId: string | null;
  organizationId: string | null;
  project: any = null;

  constructor(private dialog: MatDialog, private resultToaster: ResultToasterService,
    private http: HttpRequestService, private route: ActivatedRoute) {
      // itentionally empty
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.projectId = params.get("projectId");
      this.organizationId = params.get("organizationId");
      this.loadProject();
    })    
  }

  loadProject() {
    this.http.get("api/organization/" + this.organizationId + "/project/" + this.projectId).subscribe((res) => {
      this.project = res;
    })
  }

  dropTask(event: CdkDragDrop<string[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );
    }
  }

  dropGroup(event: CdkDragDrop<TaskGroup[]>) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
  }

  _loadOrganizations() {
    // this.http.get("api/organization/list").subscribe((res) => {
    //   this.organizations = res;
    // })
  }

  openOrganizationInviteDialog() {
    let dialogRef = this.dialog.open(OrganizationAddComponent, {
      data: {
        projectId: this.projectId,
      }
    });
    dialogRef.componentInstance.onOrganizationSelected.subscribe(organizationId => {
      this.http.post("api/organization-in-project/invite", {
        projectId: this.projectId,
        organizationId: organizationId
      }).subscribe(res => {
        if(res.success) {
          this.resultToaster.success("Organization invited successfully");
          this._loadOrganizations();
        }
        else {
          this.resultToaster.error("Unknown error");
        }
        dialogRef.close();
      })
    });
  }

}

interface TaskGroup {
  groupName: string,
  tasks: string[]
}
