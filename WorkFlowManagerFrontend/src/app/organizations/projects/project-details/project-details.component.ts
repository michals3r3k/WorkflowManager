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
import { TaskDetailsComponent } from '../task-details/task-details.component';
import { DeleteGroupConfirmComponent } from '../delete-group-confirm/delete-group-confirm.component';
import { AddStatusComponent } from '../add-status/add-status.component';

@Component({
  selector: 'app-project-details',
  templateUrl: './project-details.component.html',
  styleUrl: './project-details.component.css'
})
export class ProjectDetailsComponent {
  taskGroups: TaskGroup[] = [
    {
      groupName: "Group1",
      tasks: [new Task('Get to work'), new Task('Pick up groceries'), new Task('Go home'), new Task('Fall asleep')],
      collapsed: false
    },
    {
      groupName: "Group2",
      tasks: [new Task('Get to work'), new Task('Pick up groceries'), new Task('Go home'), new Task('Fall asleep')],
      collapsed: false
    },
    {
      groupName: "Group3",
      tasks: [new Task('Get to work'), new Task('Pick up groceries'), new Task('Go home'), new Task('Fall asleep')],
      collapsed: false
    },
  ];

  projectId: string | null;
  organizationId: string | null;
  project: any = null;

  constructor(private dialog: MatDialog, private resultToaster: ResultToasterService,
    private http: HttpRequestService, private route: ActivatedRoute) {
      // itentionally empty
  }

  // Prevent inside clicks from triggering the outside click listener
  onInsideClick(event: Event) {
    event.stopPropagation();
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

  dropTask(event: CdkDragDrop<Task[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );

      //TODO - podmienić status po stronie serwera
      event.container.data[event.currentIndex].status = this.taskGroups.find(tg => tg.tasks === event.container.data)?.groupName ?? "";
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

  openTaskDetails(task: any) {
    const dialogRef = this.dialog.open(TaskDetailsComponent, {
      data: {task: task},
      width: '80vw',
      height: '80vh',
      maxWidth: '80vw',
      maxHeight: '80vh',
    });
  }

  collapseOpenGroup(group: TaskGroup) {
    group.collapsed = !group.collapsed;
  }

  deleteGroup(group: TaskGroup) {
    if (this.taskGroups.length === 1) {
      this.resultToaster.info("You can not delete all statuses.");
      return;
    }
    if (group.tasks.length > 0) {
      this.resultToaster.info("You can not delete status with tasks.");
      return;
    }
    const dialogRef = this.dialog.open(DeleteGroupConfirmComponent, {
      data: {group: group},
      width: '250px',
      height: '150px',
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result === undefined) {
        return;
      }
      if (result) {
        this.taskGroups = this.taskGroups.filter(g => g !== group);
      }
    });
  }

  addGroup() {
    const dialogRef = this.dialog.open(AddStatusComponent, {
      width: '280px',
      height: '150px',
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result === undefined) {
        return;
      }
      if (this.taskGroups.find(g => g.groupName.toLocaleLowerCase().trim() === result.toLocaleLowerCase())) {
        this.resultToaster.error("You con not add status with the same name as existsing status.")
      }

      //TODO - dodawanie statusu po stronie serwera
      let task = new TaskGroup();
      task.groupName = result;
      this.taskGroups.push(task);
    });
  }

  onAddTaskClicked(eventData: string, group: TaskGroup) {
    group.tasks.push(new Task(eventData));
  }


  edit(project: any) {
    console.log(project);
  }

}

export class TaskGroup {
  groupName: string = "";
  tasks: Task[] = [];
  collapsed: boolean = false;
}

export class Task {
  task_id: string = "00001";
  name: string = "";
  desc: string = "";
  connected_tasks: Task[] = [];
  sub_tasks: Task[] = [];
  creator: User | null = null;
  assignUser: User | null = null;
  create_date: Date | null = new Date();
  start_date: Date | null = null;
  finish_date: Date | null = null;
  deadline: Date | null = null;
  status: string | null = "";
  priority: TaskPriority = TaskPriority.Medium;
  relation_to_parent: ConnectedTaskRelation = ConnectedTaskRelation.RelativeTo;


  constructor(name: string) {
    this.name = name;
  }
}

export enum TaskPriority {
  Low = "Low",
  Medium = "Medium",
  High = "High",
  Highest = "Highest"
}

class User {
  name: string;
  constructor(name: string) {
    this.name = name;
  }
}

enum ConnectedTaskRelation {
  Blocks = "blocks",
  BlockedBy = "Blocked by",
  RelativeTo = "Relative to"
}
