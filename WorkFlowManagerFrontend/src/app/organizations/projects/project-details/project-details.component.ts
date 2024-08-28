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
  taskGroups: TaskGroup[];

  projectId: number | null;
  organizationId: number | null;
  project: any = null;

  constructor(private dialog: MatDialog, private resultToaster: ResultToasterService,
    private http: HttpRequestService, private route: ActivatedRoute) {
      this.taskGroups = [];
  }

  // Prevent inside clicks from triggering the outside click listener
  onInsideClick(event: Event) {
    event.stopPropagation();
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const projectId = params.get("projectId");
      const organizationId = params.get("organizationId");
      this.projectId = projectId == null ? null : +projectId;
      this.organizationId = organizationId == null ? null : +organizationId;
      this.loadTasks();
    })    
  }

  loadTasks() {
    if(!this.projectId || !this.organizationId) {
      return;
    }
    this.http.getGeneric<TaskColumnRest[]>(`api/organization/${this.organizationId}/project/${this.projectId}/task/columns`).subscribe(taskColumnsRest => {
      this.taskGroups = taskColumnsRest.map(taskColumnRest => this._getTaskGroup(taskColumnRest));
    });
  }

  _getTaskGroup(taskColumnRest: TaskColumnRest): TaskGroup {
    const tasks: Task[] = taskColumnRest.tasks.map(taskRest => {
      const task = new Task(taskRest.title);
      task.status = taskColumnRest.name;
      task.taskId = taskRest.taskId;
      return task;
    });  
    const group = new TaskGroup();
    group.id = taskColumnRest.id;
    group.collapsed = false;
    group.groupName = taskColumnRest.name;
    group.tasks = tasks;
    return group;
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

  openTaskDetails(task: Task) {
    const dialogRef = this.dialog.open(TaskDetailsComponent, {
      data: {task: task, statuses: this.taskGroups.map(g => g.groupName)},
      width: '80vw',
      height: '80vh',
      maxWidth: '80vw',
      maxHeight: '80vh',
    });

    dialogRef.afterClosed().subscribe(result => {
      const previousGroup = this.taskGroups.find(g => g.tasks.some(t => t === task))
      if (previousGroup) {
        previousGroup.tasks = previousGroup.tasks.filter(t => t !== task);
      }
      const newGroup = this.taskGroups.find(g => g.groupName.toLowerCase() === task.status?.toLowerCase())
      newGroup?.tasks.push(task);
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
      data: {organizationId: this.organizationId, projectId: this.projectId},
      width: '280px',
      height: '150px',
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadTasks();
      }

      // //TODO - dodawanie statusu po stronie serwera
      // let task = new TaskGroup();
      // task.groupName = result;
      // this.taskGroups.push(task);
    });
  }

  onAddTaskClicked(taskTitle: string, group: TaskGroup) {
    let task = new Task(taskTitle);
    task.status = group.groupName;
    group.tasks.push(task);
  }


  edit(project: any) {
    console.log(project);
  }

}

interface TaskMemberRest {
  userId: number;
  email: string;
}

interface TaskRest {
  taskId: number;
  title: string;
  members: TaskMemberRest[];
}

interface TaskColumnRest {
  id: number;
  name: string;
  tasks: TaskRest[];
}

export class TaskGroup {
  id: number;
  groupName: string = "";
  tasks: Task[] = [];
  collapsed: boolean = false;
}

class Task {
  taskId: number;
  title: string;
  assignUser: User | null = null;
  status: string | null = "Group1";

  constructor(name: string) {
    this.title = name;
  }
}

export enum TaskPriority {
  Low = "Low",
  Medium = "Medium",
  High = "High",
  Highest = "Highest"
}

class User {
  userId: number;
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
