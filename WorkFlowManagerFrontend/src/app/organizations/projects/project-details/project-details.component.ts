import { Component, OnDestroy, OnInit, Pipe, PipeTransform } from '@angular/core';
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
import { ServiceResult } from '../../../services/utils/service-result';
import { ServiceResultHelper } from '../../../services/utils/service-result-helper';
import { WebsocketService } from '../../../services/websocket/websocket.service';

@Component({
  selector: 'app-project-details',
  templateUrl: './project-details.component.html',
  styleUrl: './project-details.component.css'
})
export class ProjectDetailsComponent implements OnInit, OnDestroy {
  taskGroups: TaskGroup[];

  projectId: number | null;
  organizationId: number | null;
  project: any = null;

  constructor(private dialog: MatDialog, private resultToaster: ResultToasterService,
    private serviceResultHelper: ServiceResultHelper,
    private http: HttpRequestService, private route: ActivatedRoute,
    private webSocketService: WebsocketService) {
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
      this.webSocketService.connect();
    });
  }

  ngOnDestroy(): void {
    this.webSocketService.disconnect();
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
      task.priority = taskRest.priority;
      if(taskRest.members.length !== 0) {
        const taskMember: TaskMemberRest = taskRest.members[0];
        const user = new User(taskMember.email);
        user.userId = taskMember.userId;
        task.assignUser = user;
      }
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
    if(event.previousContainer === event.container && event.previousIndex === event.currentIndex) {
      return;
    }
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
    const taskOrderList: {taskId: number, taskColumnId: number, order: number}[] = [];
    for(let i = 0; i < this.taskGroups.length; ++i) {
      const taskGroup = this.taskGroups[i];
      for(let j = 0; j < taskGroup.tasks.length; ++j) {
        const task = taskGroup.tasks[j];
        taskOrderList.push({
          taskId: task.taskId,
          taskColumnId: taskGroup.id,
          order: j
        });
      }
    }
    this.http.postGeneric<ServiceResult>(`api/organization/${this.organizationId}/project/${this.projectId}/task/column/change-task-order`, taskOrderList).subscribe(res => {
      this.serviceResultHelper.handleServiceResult(res, "Task moved succesfully", "Errors occured");
      if(!res.success) {
        this.loadTasks();
      }
    });
  }

  dropGroup(event: CdkDragDrop<TaskGroup[]>) {
    if(event.previousIndex === event.currentIndex) {
      return;
    }
    moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    const columnOrderList: {taskColumnId: number, order: number}[] = [];
    const data = event.container.data;
    for(let i = 0; i < data.length; ++i) {
      columnOrderList.push({
        taskColumnId: data[i].id,
        order: i,
      });
    }
    this.http.postGeneric<ServiceResult>(`api/organization/${this.organizationId}/project/${this.projectId}/task/column/change-order`, columnOrderList).subscribe(res => {
      this.serviceResultHelper.handleServiceResult(res, "Column moved succesfully", "Errors occured");
      if(!res.success) {
        this.loadTasks();
      }
    });
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
      data: {organizationId: this.organizationId, projectId: this.projectId, taskId: task.taskId},
      width: '80vw',
      height: '80vh',
      maxWidth: '80vw',
      maxHeight: '80vh',
    });

    dialogRef.afterClosed().subscribe(result => {
      this.loadTasks();
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
        this.http.getGeneric<ServiceResult>(`api/organization/${this.organizationId}/project/${this.projectId}/task/column/${group.id}/delete`).subscribe(res => {
          this.serviceResultHelper.handleServiceResult(res, "Column deleted successfully", "Errors occured");
          if(res.success) {
            this.taskGroups = this.taskGroups.filter(g => g !== group);
          }
        });
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
    });
  }

  onAddTaskClicked(taskTitle: string, group: TaskGroup) {
    this.http.postGeneric<{taskIdOrNull: number | null, success: boolean, errors: [string]}>(`api/organization/${this.organizationId}/project/${this.projectId}/task/column/add-task`, {title: taskTitle, taskColumnId: group.id}).subscribe(res => {
      this.serviceResultHelper.handleServiceResult(res as ServiceResult, "Task created succefully", "Errors occured");
      this.loadTasks();
    });
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
  members: TaskMemberRest[]
  priority: TaskPriority;
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
  status: string | null;
  priority: TaskPriority;

  constructor(name: string) {
    this.title = name;
  }
}

export enum TaskPriority {
  VERY_LOW = "VERY_LOW",
  LOW = "LOW",
  MEDIUM = "MEDIUM",
  HIGH = "HIGH",
  VERY_HIGH = "VERY_HIGH"
}

class User {
  userId: number;
  name: string;
  constructor(name: string) {
    this.name = name;
  }
}
