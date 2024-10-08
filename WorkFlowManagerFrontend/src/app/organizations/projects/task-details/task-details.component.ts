import { Component, Inject, OnInit, Pipe, PipeTransform, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { debounceTime, map, Observable, of, startWith, switchMap } from 'rxjs';
import { HttpRequestService } from '../../../services/http/http-request.service';
import { ServiceResult } from '../../../services/utils/service-result';
import { ServiceResultHelper } from '../../../services/utils/service-result-helper';
import { TaskPriority } from '../project-details/project-details.component';
import { ChatComponent } from '../../../chat/chat.component';

@Component({
  selector: 'app-task-details',
  templateUrl: './task-details.component.html',
  styleUrls: ['./task-details.component.css']
})
export class TaskDetailsComponent implements OnInit {
  taskId: number;
  organizationId: number;
  projectId: number;

  isDescritionEditing: boolean = false;
  isTitleEditing: boolean = false;
  isAssignToEditing: boolean = false;
  isStartDateEditing: boolean = false; 
  isFinishDateEditing: boolean = false;
  isDeadlineEditing: boolean = false;
  isPriorityEditing: boolean = false;
  isStatusEditing: boolean = false;

  isAddingConnectedTask: boolean = false;
  isAddingSubTask: boolean = false;

  description: string = "";
  title: string = "";
  start_date: Date | null = null;
  finish_date: Date | null = null;
  deadline: Date | null = null;
  selectedPriority: TaskPriority = TaskPriority.MEDIUM;
  selectedConnectedTaskRelation: ConnectedTaskRelation = ConnectedTaskRelation.IS_RELATIVE_TO;
  selectedConnectedTask: TaskRelation | null = null;
  selectedAssignUser: User | null = null;

  userOptions$: Observable<User[]>;
  taskRelationOptions$: Observable<TaskRelationOptionRest[]>;
  connectedTaskOptions$: Observable<TaskRelationOptionRest[]>;
  assignUserOptions$: Observable<any[]>;

  new_sub_task_name = "";

  searchConnectedTaskControl = new FormControl();
  searchAssignUserControl = new FormControl();

  taskPriorityOptions = Object.values(TaskPriority);
  taskRelations = Object.values(ConnectedTaskRelation);

  task: Task;

  constructor(
    @Inject(MAT_DIALOG_DATA) private data: {organizationId: number, projectId: number, taskId: number},
    private dialogRef: MatDialogRef<TaskDetailsComponent>,
    private dialog: MatDialog,
    private serviceResultHelper: ServiceResultHelper,
    private http: HttpRequestService) {

    this.organizationId = data.organizationId;
    this.projectId = data.projectId;
    this.taskId = data.taskId;
  }

  private loadSearchConnectedTask(): Observable<any[]> {
    const searchTerm = this.searchConnectedTaskControl.value?.toLowerCase() || '';
    return this.taskRelationOptions$.pipe(
      switchMap(tasks => {
        const filteredTasks = tasks.filter(task =>
          task.title.toLowerCase().includes(searchTerm)
        );
        return of(filteredTasks);
      })
    );
  }

  private loadSearchAssignUsers(): Observable<any[]> {
    const searchTerm = this.searchAssignUserControl.value?.toLowerCase() || '';
    return this.userOptions$.pipe(
      switchMap(users => {
        const filteredUsers = users.filter(user =>
          user.name.toLowerCase().includes(searchTerm)
        );
        return of(filteredUsers);
      })
    );
  }

  onTaskOptionSelected(event: MatAutocompleteSelectedEvent): void {
    const selectedTaskName = event.option.value;
    this.taskRelationOptions$.pipe(
      map(tasks => {
        const task = tasks.find(task => task.title === selectedTaskName);
        if(!task) {
          return null;
        }
        const taskRelation: TaskRelation = {
          taskId: task.taskId,
          title: task.title,
          relationType: ConnectedTaskRelation.IS_RELATIVE_TO,
          columnName: task.columnName
        };
        return taskRelation;
      })).subscribe(subTask => {
        this.selectedConnectedTask = subTask; 
      });
  }

  onAssignOptionSelected(event: MatAutocompleteSelectedEvent): void {
    const selectedAssignUserName = event.option.value;
    this.userOptions$.subscribe(users => {
      this.selectedAssignUser = users.find(user => user.name === selectedAssignUserName) || null;
    });
  }

  _saveTask(resetEditableProperties: boolean = false) {
    this.task.task_id;
    const members: TaskMemberRest[] = !this.task.assignUser || !this.task.assignUser.userId ? [] : [{userId: this.task.assignUser.userId, email: this.task.assignUser.name}]
    const taskRelations: TaskRelationRest[] = this.task.connected_tasks.map(taskRelation => {
      return {taskId: taskRelation.taskId, title: taskRelation.title, relationType: taskRelation.relationType, columnName: taskRelation.columnName};
    });
    const subTasks: SubTaskRest[] = this.task.sub_tasks.map(subTask => {
      return {subTaskId: subTask.task_id, title: subTask.title};
    })
    const taskRest: TaskRest = {
      taskId: this.task.task_id,
      chatId: this.task.chatId,
      title: this.task.name,
      descriptionOrNull: this.task.desc,
      creatorId: this.task.creatorId,
      creatorName: this.task.creatorName,
      createTime: this.task.create_date.toISOString(),
      startDateOrNull: this.task.start_date?.toISOString() || null,
      finishDateOrNull: this.task.finish_date?.toISOString() || null,
      deadlineDateOrNull: this.task.deadline?.toISOString() || null,
      parentTaskIdOrNull: this.task.parentTaskIdOrNull,
      parentTaskTitleOrNull: this.task.parentTaskTitleOrNull,
      parentTaskPriorityOrNull: this.task.parentTaskPriorityOrNull,
      priority: this.task.priority,
      members: members,
      subTasks: subTasks,
      taskRelations: taskRelations,
      columnName: this.task.status
    }
    this.http.postGeneric<TaskEditServiceResult>(`api/organization/${this.organizationId}/project/${this.projectId}/task/save`, taskRest).subscribe(res => {
      this.serviceResultHelper.handleServiceResult(res as ServiceResult, "Task saved successfully", "Errors occured");
      if(res.success) {
        this._initTaskRest(res.taskRest, resetEditableProperties);
      }
    });
  }

  ngOnInit() {
    this._init();
  }

  _init() {
    this.http.getGeneric<TaskRest>(`api/organization/${this.organizationId}/project/${this.projectId}/task/${this.taskId}`).subscribe(taskRest => {
      this._initTaskRest(taskRest, true);
    });

    this.userOptions$ = this.http.getGeneric<TaskMemberOptionRest[]>(`api/organization/${this.organizationId}/task/member/options`).pipe(
      map(usersRest => usersRest.map(userRest => {
        const user = new User(userRest.name);
        user.userId = userRest.userId;
        return user;
      }))
    );
    this.taskRelationOptions$ = this.http.getGeneric<TaskRelationOptionRest[]>(
      `api/organization/${this.organizationId}/project/${this.projectId}/task/${this.taskId}/relation/options`);

    this.connectedTaskOptions$ = this.searchConnectedTaskControl.valueChanges
      .pipe(
        startWith(''),
        debounceTime(500),
        switchMap(() => { return this.loadSearchConnectedTask(); })
    );

    this.assignUserOptions$ = this.searchAssignUserControl.valueChanges
      .pipe(
        startWith(''),
        debounceTime(500),
        switchMap(() => { return this.loadSearchAssignUsers(); })
    );
  }

  _initTaskRest(taskRest: TaskRest, resetEditableProperties: boolean = false) {
    this.title = taskRest.title;
    const task = new Task(taskRest.title);
    task.task_id = taskRest.taskId;
    task.desc = taskRest.descriptionOrNull || "";
    task.chatId = taskRest.chatId;
    task.creatorId = taskRest.creatorId;
    task.creatorName = taskRest.creatorName;
    task.create_date = new Date(taskRest.createTime);
    task.start_date = taskRest.startDateOrNull ? new Date(taskRest.startDateOrNull) : null;
    task.finish_date = taskRest.finishDateOrNull ? new Date(taskRest.finishDateOrNull) : null;
    task.deadline = taskRest.deadlineDateOrNull? new Date(taskRest.deadlineDateOrNull) : null;
    task.parentTaskIdOrNull = taskRest.parentTaskIdOrNull;
    task.parentTaskTitleOrNull = taskRest.parentTaskTitleOrNull;
    task.parentTaskPriorityOrNull = taskRest.parentTaskPriorityOrNull;
    task.isSubTask = !!taskRest.parentTaskIdOrNull;
    task.priority = taskRest.priority;
    task.status = taskRest.columnName || "unassigned";
    if(taskRest.members.length !== 0) {
      const taskMemberRest: TaskMemberRest = taskRest.members[0];
      const assignUser = new User(taskMemberRest.email || "");
      assignUser.userId = taskMemberRest.userId;
      task.assignUser = assignUser;
      this.searchAssignUserControl.setValue(assignUser.name);
    }
    else {
      this.searchAssignUserControl.setValue("");
    }
    task.connected_tasks = taskRest.taskRelations.map(taskRelationRest => {
      const taskRelation = new TaskRelation();
      taskRelation.taskId = taskRelationRest.taskId;
      taskRelation.title = taskRelationRest.title;
      taskRelation.relationType = taskRelationRest.relationType;
      taskRelation.columnName = taskRelationRest.columnName || "unassigned";
      return taskRelation;
    });
    task.sub_tasks = taskRest.subTasks.map(subTaskRest => {
      const subTask = new SubTask();
      subTask.task_id = subTaskRest.subTaskId;
      subTask.title = subTaskRest.title;
      subTask.priority = TaskPriority.MEDIUM; 
      return subTask;
    });
    this.task = task;
    if(resetEditableProperties) {
      this.title = this.task.name;
      this.description = this.task.desc;
      this.selectedPriority = this.task.priority;
      this.selectedAssignUser = this.task.assignUser;
      this.start_date = this.task.start_date;
      this.finish_date = this.task.finish_date;
      this.deadline = this.task.deadline;
    }
  }

  close() {
    this.dialogRef.close();
  }

  save() {
    this._saveTitle();
    this._saveDescription();
    this._savePriority();
    this._saveAssignTo();
    this._saveStartDate();
    this._saveFinishDate();
    this._saveDeadline();
    this._saveTask(true);
  }

  openTaskDetails(taskId: number | null) {
    if(taskId) {
      this.taskId = taskId;
      this._init();
    }
  }

  onDescriptionFocus() {
    this.isDescritionEditing = true;
  }

  onDescriptionBlur() {
    this.isDescritionEditing = false;
  }

  saveDescription() {
    this._saveDescription();
    this._saveTask();
  }

  _saveDescription() {
    this.task.desc = this.description;
  }

  cancelDescriptionEdit() {
    this.description = this.task.desc;
  }

  onTitleFocus() {
    this.isTitleEditing = true;
  }

  onTitleBlur() {
    this.isTitleEditing = false;
  }

  onAssignToFocus() {
    this.isAssignToEditing = true;
  }

  onAssignToBlur() {
    this.isAssignToEditing = false;
  }

  saveAssignTo() {
    this._saveAssignTo();
    this._saveTask();
  }

  _saveAssignTo() {
    this.task.assignUser = this.selectedAssignUser;
  }

  cancelAssignToEdit() {
    this.selectedAssignUser = this.task.assignUser;
    this.searchAssignUserControl.setValue(this.selectedAssignUser?.name ?? "");
  }

  saveTitle() {
    this._saveTitle();
    this._saveTask();
  }

  _saveTitle() {
    this.task.name = this.title;
  }

  cancelTitleEdit() {
    this.title = this.task.name;
  }

  addConnectedTask() {
    this.isAddingConnectedTask = true;
  }

  addSubTask() {
    this.isAddingSubTask = true;
  }

  cancelAddSubTask() {
    this.isAddingSubTask = false;
  }

  cancelAddConnectedTask() {
    this.isAddingConnectedTask = false;
  }

  // START DATE
  onStartDateFocus() {
    this.isStartDateEditing = true;
  }

  onStartDateBlur() {
    this.isStartDateEditing = false;
  }

  saveStartDate() {
    this._saveStartDate();
    this._saveTask();
  }

  _saveStartDate() {
    this.task.start_date = this.start_date;
  }

  cancelStartDate() {
    this.start_date = this.task.start_date;
  }

  // FINISH DATE
  onFinishDateFocus() {
    this.isFinishDateEditing = true;
  }

  onFinishDateBlur() {
    this.isFinishDateEditing = false;
  }

  saveFinishDate() {
    this._saveFinishDate();
    this._saveTask();
  }

  _saveFinishDate() {
    this.task.finish_date = this.finish_date;
  }

  cancelFinishDate() {
    this.finish_date = this.task.finish_date;
  }

  // DEADLINE
  onDeadlineFocus() {
    this.isDeadlineEditing = true;
  }

  onDeadlineBlur() {
    this.isDeadlineEditing = false;
  }

  saveDeadline() {
    this._saveDeadline();
    this._saveTask();
  }

  _saveDeadline() {
    this.task.deadline = this.deadline;
  }

  cancelDeadline() {
    this.deadline = this.task.deadline;
  }


  // PRIORITY
  onPriorityFocus() {
    this.isPriorityEditing = true;
  }

  onPriorityBlur() {
    this.isPriorityEditing = false;
  }

  savePriority() {
    this._savePriority();
    this._saveTask();
  }

  _savePriority() {
    this.task.priority = this.selectedPriority;
  }

  cancelPriority() {
    this.selectedPriority = this.task.priority;
  }

  // STATUS
  onStatusFocus() {
    this.isStatusEditing = true;
  }

  onStatusBlur() {
    this.isStatusEditing = false;
  }

  onAddConnectedTaskInputChange() {
    const inputValue = this.searchConnectedTaskControl.value;
    if (!this.selectedConnectedTask || this.selectedConnectedTask.title !== inputValue) {
      this.selectedConnectedTask = null;
    }
  }

  onAssignToInputChange() {
    const inputValue = this.searchAssignUserControl.value;
    if (!this.selectedAssignUser || this.selectedAssignUser.name !== inputValue) {
      this.selectedAssignUser = null;
    }
  }

  createNewSubTask() {
    if(this.new_sub_task_name) {
      let sub_task = new SubTask();
      sub_task.title = this.new_sub_task_name;
      this.task.sub_tasks.push(sub_task);
      this.new_sub_task_name = "";
      this.isAddingSubTask = false;
      this._saveTask();
    }
  }

  deleteTask() {
    this.http.get(`api/organization/${this.organizationId}/project/${this.projectId}/task/${this.taskId}/delete`).subscribe(() => {
      this.serviceResultHelper.handleServiceResult({success: true, errors: ['']}, "Task deleted successfully", "Errors occured");
      this.close();
    });
  }

  addSelectedConnectedTask() {
    if (this.selectedConnectedTask !== null) {
      this.selectedConnectedTask.relationType = this.selectedConnectedTaskRelation;
      this.task.connected_tasks.push(this.selectedConnectedTask);
      this.searchConnectedTaskControl.setValue("");
      this.selectedConnectedTask = null;
      this._saveTask();
    }
  }

  deleteSubTask(task: SubTask) {
    this.task.sub_tasks = this.task.sub_tasks.filter(st => st !== task);
  }

  deleteConnectedTask(task: TaskRelation) {
    this.task.connected_tasks = this.task.connected_tasks.filter(ct => ct !== task);
  }

}

class Task {
  task_id: number;
  chatId: number;
  name: string = "";
  desc: string = "";
  creatorId: number;
  creatorName: string;
  connected_tasks: TaskRelation[] = [];
  sub_tasks: SubTask[] = [];
  creator: User | null = null;
  assignUser: User | null = null;
  create_date: Date = new Date();
  start_date: Date | null = null;
  finish_date: Date | null = null;
  deadline: Date | null = null;
  status: string;
  priority: TaskPriority = TaskPriority.MEDIUM;
  parentTaskIdOrNull: number | null = null;
  parentTaskTitleOrNull: string | null = null;
  parentTaskPriorityOrNull: TaskPriority | null = null;
  isSubTask: boolean = false;

  constructor(name: string) {
    this.name = name;
  }
}

class SubTask {
  task_id: number | null;
  title: string;
  status: string | null;
  priority: TaskPriority;
}

class TaskRelation {
  taskId: number;
  title: string;
  relationType: ConnectedTaskRelation;
  columnName: string;
}

class User {
  userId: number;
  name: string;
  constructor(name: string) {
    this.name = name;
  }
}

enum ConnectedTaskRelation {
  BLOCKS = "BLOCKS",
  IS_BLOCKED_BY = "IS_BLOCKED_BY",
  IS_RELATIVE_TO = "IS_RELATIVE_TO"
}

interface TaskRest {
  taskId: number;
  chatId: number;
  title: string;
  descriptionOrNull: string | null;
  createTime: string;
  creatorId: number;
  creatorName: string;
  startDateOrNull: string | null;
  finishDateOrNull: string | null;
  deadlineDateOrNull: string | null;
  parentTaskIdOrNull:number | null;
  parentTaskTitleOrNull: string | null;
  parentTaskPriorityOrNull: TaskPriority | null;
  columnName: string | null;
  priority: TaskPriority;
  members: TaskMemberRest[];
  subTasks: SubTaskRest[];
  taskRelations: TaskRelationRest[];
}

interface TaskMemberRest {
  userId: number;
  email?: string;
}

interface SubTaskRest {
  subTaskId: number | null;
  title: string;
}

interface TaskRelationRest {
  taskId: number;
  title: string;
  relationType: ConnectedTaskRelation;
  columnName: string;
}

interface TaskMemberOptionRest {
  userId: number;
  name: string
}

interface TaskRelationOptionRest {
  taskId: number;
  title: string;
  columnName: string;


}

export interface TaskEditServiceResult {
  taskRest: TaskRest,
  success: boolean,
  errors: [string]
};

@Pipe({
  name: 'connectedTaskRelationTranslation'
})
export class ConnectedTaskRelationTranslationPipe implements PipeTransform {

  transform(taskRelation: ConnectedTaskRelation | null): string {
    if (!taskRelation) {
      return "";
    }
    if(taskRelation == ConnectedTaskRelation.BLOCKS) {
      return "blocks";
    }
    if(taskRelation == ConnectedTaskRelation.IS_BLOCKED_BY) {
      return "is blocked by";
    }
    if(taskRelation == ConnectedTaskRelation.IS_RELATIVE_TO) {
      return "is relative to";
    }
    return "";
  }

}
