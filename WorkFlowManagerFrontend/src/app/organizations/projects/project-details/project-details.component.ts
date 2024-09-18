import { Component, OnDestroy, OnInit, Pipe, PipeTransform, ViewChild } from '@angular/core';
import { CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
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
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-project-details',
  templateUrl: './project-details.component.html',
  styleUrl: './project-details.component.css'
})
export class ProjectDetailsComponent implements OnInit, OnDestroy {
  @ViewChild(MatSort) sort: MatSort;

  displayedColumns: string[] = ['image','taskId','status','title','assigned-to','priority'];

  unassignedTasksColumn: TaskColumn;
  taskColumns: TaskColumn[];
  flatTasksList: Task[] = [];
  filteredTaskList: Task[] = [];
  filteredDataSource = new MatTableDataSource(this.filteredTaskList);

  projectId: number | null;
  organizationId: number | null;
  project: any = null;

  //FILTERING
  searchTextControl: FormControl = new FormControl();
  filterSearchText: string = "";

  taskPriorityOptions = [ null, ...Object.values(TaskPriority)];
  _filterPriority: TaskPriority | null = null;
  get filterPriority(): TaskPriority | null { return this._filterPriority; }
  set filterPriority(value: TaskPriority | null) {this._filterPriority = value; this.filterTasks() }

  taskStatusOptions: (string | null)[] = [ null ];
  _filterStatus: string | null = null;
  get filterStatus(): string | null { return this._filterStatus; }
  set filterStatus(value: string|null) { this._filterStatus = value; this.filterTasks() }

  taskAssignUserOptions: (string | null)[] = [ null ];
  _filterUser: string | null = null;
  get filterUser(): string | null { return this._filterUser; }
  set filterUser(value: string | null) { this._filterUser = value; this.filterTasks() }

  constructor(private dialog: MatDialog, private resultToaster: ResultToasterService,
    private serviceResultHelper: ServiceResultHelper,
    private http: HttpRequestService, private route: ActivatedRoute,
    private webSocketService: WebsocketService) {
      this.unassignedTasksColumn = {
        collapsed: false,
        name: "Unassigned tasks",
        id: null,
        tasks: [],
      };
      this.taskColumns = [];

      this.searchTextControl.valueChanges.pipe(debounceTime(400)).subscribe(value =>{
        this.filterSearchText = value;
        this.filterTasks();
      })
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
      const taskId = params.get("taskId");
      this._openTaskDetails(taskId ? +taskId : null);
      this.loadTasks();
      this.loadProject();
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
      this.unassignedTasksColumn.tasks = this._getTasks(taskColumnsRest
        .filter(taskColumnRest => !taskColumnRest.id)
        .map(taskColumnRest => taskColumnRest.tasks)[0] || []);
      this.taskColumns = taskColumnsRest
        .filter(taskColumnRest => taskColumnRest.id)
        .map(taskColumnRest => this._getTaskColumn(taskColumnRest));
        this.flatTasksList = this.taskColumns.flatMap(group => group.tasks);
        this.flatTasksList = this.flatTasksList.concat(this.unassignedTasksColumn.tasks);
        this.taskStatusOptions = [...new Set( this.flatTasksList.map(task => task.status))];
        this.taskAssignUserOptions = [null, ...new Set( this.flatTasksList.filter(task => task.assignUser !== null).map(task => task.assignUser!.name))];
        this.filterTasks();
      });
  }

  _getTaskColumn(taskColumnRest: TaskColumnRest): TaskColumn {
    const tasks: Task[] = this._getTasks(taskColumnRest.tasks);
    const column = new TaskColumn();
    column.id = taskColumnRest.id;
    column.collapsed = false;
    column.name = taskColumnRest.name;
    column.tasks = tasks;
    return column;
  }

  _getTasks(tasks: TaskRest[]): Task[] {
    return tasks.map(taskRest => {
      const task = new Task(taskRest.title);
      task.taskId = taskRest.taskId;
      task.priority = taskRest.priority;
      task.parentTaskIdOrNull = taskRest.parentTaskIdOrNull;
      task.parentTaskTitleOrNull = taskRest.parentTaskTitleOrNull;
      task.status = taskRest.columnNameOrNull;
      if(taskRest.members.length !== 0) {
        const taskMember: TaskMemberRest = taskRest.members[0];
        const user = new User(taskMember.email);
        user.userId = taskMember.userId;
        task.assignUser = user;
      }
      return task;
    });
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
    }
    const taskOrderList: {taskId: number, taskColumnId: number | null, order: number}[] = [];
    const taskColumns = [this.unassignedTasksColumn, ...this.taskColumns];
    for(let i = 0; i < taskColumns.length; ++i) {
      const taskColumn = taskColumns[i];
      for(let j = 0; j < taskColumn.tasks.length; ++j) {
        const task = taskColumn.tasks[j];
        taskOrderList.push({
          taskId: task.taskId,
          taskColumnId: taskColumn.id,
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

  dropGroup(event: CdkDragDrop<TaskColumn[]>) {
    if(event.previousIndex === event.currentIndex) {
      return;
    }
    moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    const columnOrderList: {taskColumnId: number | null, order: number}[] = [];
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
    this._openTaskDetails(task.taskId);
  }

  _openTaskDetails(taskId: number | null) {
    if(!taskId) {
      return;
    }
    const dialogRef = this.dialog.open(TaskDetailsComponent, {
      data: {organizationId: this.organizationId, projectId: this.projectId, taskId: taskId},
      width: '80vw',
      height: '80vh',
      maxWidth: '80vw',
      maxHeight: '80vh',
    });

    dialogRef.afterClosed().subscribe(result => {
      this.loadTasks();
    });
  }

  collapseOpenGroup(column: TaskColumn) {
    column.collapsed = !column.collapsed;
  }

  deleteColumn(column: TaskColumn) {
    if (column.tasks.length > 0) {
      this.resultToaster.info("You can not delete status with tasks.");
      return;
    }
    const dialogRef = this.dialog.open(DeleteGroupConfirmComponent, {
      data: {group: column},
      width: '250px',
      height: '150px',
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result === undefined) {
        return;
      }
      if (result) {
        this.http.getGeneric<ServiceResult>(`api/organization/${this.organizationId}/project/${this.projectId}/task/column/${column.id}/delete`).subscribe(res => {
          this.serviceResultHelper.handleServiceResult(res, "Column deleted successfully", "Errors occured");
          if(res.success) {
            this.taskColumns = this.taskColumns.filter(g => g !== column);
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

  onAddTaskClicked(taskTitle: string, column: TaskColumn) {
    this.http.postGeneric<ServiceResult>(`api/organization/${this.organizationId}/project/${this.projectId}/task/column/add-task`, {title: taskTitle, taskColumnId: column.id}).subscribe(res => {
      this.serviceResultHelper.handleServiceResult(res, "Task created succefully", "Errors occured");
      this.loadTasks();
    });
  }

  filterTasks() {
    console.log(typeof(this._filterPriority))
    console.log(this.filterPriority)
    this.flatTasksList.forEach(task => {
      task.hidden = false;

      if (this.filterSearchText || "".length > 0){
        if (!task.title.toLowerCase().includes(this.filterSearchText.toLocaleLowerCase()) && !task.taskId.toString().includes(this.filterSearchText)){
          task.hidden = true;
        }
      }

      if (this.filterPriority !== null && this.filterPriority.toString() !== "null") {
        if (task.priority !== this.filterPriority) {
          task.hidden = true;
        }
      }

      if (this.filterStatus !== null && this.filterStatus.toString() !== "null") {
        if (task.status !== this.filterStatus) {
          task.hidden = true;
        }
      }

      if (this.filterUser !== null && this.filterUser.toString() !== "null") {
        if (task.assignUser === null || task.assignUser.name !== this.filterUser) {
          task.hidden = true;
        }
      }
    })

    this.filteredTaskList = [...this.flatTasksList.filter(t => !t.hidden)]
    this.filteredDataSource = new MatTableDataSource(this.filteredTaskList);

    this.filteredDataSource.sortingDataAccessor = (item, property) => {
      switch(property) {
        case 'assigned-to': return item.assignUser?.name;
        default: return (item as any)[property];
      }
    };

    this.filteredDataSource.sort = this.sort;
  }
}

interface TaskMemberRest {
  userId: number;
  email: string;
}

export interface TaskRest {
  taskId: number;
  title: string;
  members: TaskMemberRest[],
  priority: TaskPriority,
  parentTaskIdOrNull: number | null,
  parentTaskTitleOrNull: string | null,
  columnNameOrNull: string | null,
}

interface TaskColumnRest {
  id: number;
  name: string;
  tasks: TaskRest[];
}

export class TaskColumn {
  id: number | null;
  name: string = "";
  tasks: Task[] = [];
  collapsed: boolean = false;
}

class Task {
  taskId: number;
  title: string;
  assignUser: User | null = null;
  priority: TaskPriority;
  parentTaskIdOrNull: number | null;
  parentTaskTitleOrNull: string | null;
  status: string | null;
  hidden: boolean = false;

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
