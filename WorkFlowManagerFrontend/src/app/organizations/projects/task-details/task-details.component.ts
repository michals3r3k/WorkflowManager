import { Component, Inject, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { debounceTime, Observable, of, startWith, switchMap } from 'rxjs';

@Component({
  selector: 'app-task-details',
  templateUrl: './task-details.component.html',
  styleUrls: ['./task-details.component.css']
})
export class TaskDetailsComponent implements OnInit {

  isDescritionEditing: boolean = false;
  isTitleEditing: boolean = false;
  isCreatorEditing: boolean = false;
  isAssignToEditing: boolean = false;
  isStartDateEditing: boolean = false;
  isFinishDateEditing: boolean = false;
  isDeadlineEditing: boolean = false;
  isProgressEditing:boolean = false;

  isAddingConnectedTask: boolean = false;
  isAddingSubTask: boolean = false;

  description: string = "";
  title: string = "";
  start_date: Date | null = null;
  finish_date: Date | null = null;
  deadline: Date | null = null;
  selectedProgress: TaskProgress = TaskProgress.New;
  selectedConnectedTaskRelation: ConnectedTaskRelation = ConnectedTaskRelation.RelativeTo;

  new_sub_task_name = "";

  searchConnectedTaskControl = new FormControl();
  connectedTaskOptions$: Observable<any[]>;
  selectedConnectedTask: Task | null = null;

  searchCreatorControl = new FormControl();
  creatorOptions$: Observable<any[]>;
  selectedCreator: User | null = null;

  searchAssignUserControl = new FormControl();
  assignUserOptions$: Observable<any[]>;
  selectedAssignUser: User | null = null;

  found_tasks: Observable<Task[]> = of([
    new Task("task1"),
    new Task("test task"),
    new Task("do roboty")
  ]);

  found_creators: Observable<User[]> = of([
    new User("Tomek"),
    new User("Marta"),
    new User("Dominik"),
  ]);

  found_users: Observable<User[]> = of([
    new User("Rafał"),
    new User("Marcin"),
    new User("Kacper"),
  ]);

  taskProgressOptions = Object.values(TaskProgress);
  taskRelations = Object.values(ConnectedTaskRelation)

  task: Task = new Task("");

  constructor(
    @Inject(MAT_DIALOG_DATA) private data: {task: string},
    private dialogRef: MatDialogRef<TaskDetailsComponent>,
    private dialog: MatDialog) {
    this.title = data.task;
    this.task.name = data.task;

    this.connectedTaskOptions$ = this.searchConnectedTaskControl.valueChanges
      .pipe( 
        startWith(''),
        debounceTime(500),
        switchMap(() => { return this.loadSearchConnectedTask(); })
    )

    this.creatorOptions$ = this.searchCreatorControl.valueChanges
      .pipe( 
        startWith(''),
        debounceTime(500),
        switchMap(() => { return this.loadSearchCreators(); })
    )

    this.assignUserOptions$ = this.searchAssignUserControl.valueChanges
      .pipe( 
        startWith(''),
        debounceTime(500),
        switchMap(() => { return this.loadSearchAssignUsers(); })
    )
  }

  private loadSearchConnectedTask(): Observable<any[]> {
    const searchTerm = this.searchConnectedTaskControl.value?.toLowerCase() || '';
    return this.found_tasks.pipe(
      switchMap(tasks => {
        const filteredTasks = tasks.filter(task =>
          task.name.toLowerCase().includes(searchTerm)
        );
        return of(filteredTasks);
      })
    );
  }

  private loadSearchCreators(): Observable<any[]> {
    const searchTerm = this.searchCreatorControl.value?.toLowerCase() || '';
    return this.found_creators.pipe(
      switchMap(users => {
        const filteredUsers = users.filter(user =>
          user.name.toLowerCase().includes(searchTerm)
        );
        return of(filteredUsers);
      })
    );
  }

  private loadSearchAssignUsers(): Observable<any[]> {
    const searchTerm = this.searchAssignUserControl.value?.toLowerCase() || '';
    return this.found_users.pipe(
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
    this.found_tasks.subscribe(tasks => {
      this.selectedConnectedTask = tasks.find(task => task.name === selectedTaskName) || null;
    });
  }

  onCreatorOptionSelected(event: MatAutocompleteSelectedEvent): void {
    const selectedCreatorName = event.option.value;
    this.found_creators.subscribe(users => {
      this.selectedCreator = users.find(user => user.name === selectedCreatorName) || null;
    });
  }

  onAssignOptionSelected(event: MatAutocompleteSelectedEvent): void {
    const selectedAssignUserName = event.option.value;
    this.found_users.subscribe(users => {
      this.selectedAssignUser = users.find(user => user.name === selectedAssignUserName) || null;
    });
  }

  ngOnInit() {
  }

  close() {
    this.dialogRef.close();
  }

  onDescriptionFocus() {
    this.isDescritionEditing = true;
  }

  onDescriptionBlur() {
    this.isDescritionEditing = false;
  }

  saveDescription() {
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

  onCreatorFocus() {
    this.isCreatorEditing = true;
  }

  onCreatorBlur() {
    this.isCreatorEditing = false;
  }

  saveCreator() {
    this.task.creator = this.selectedCreator;
  }

  cancelCreatorEdit() {
    this.selectedCreator = this.task.creator;
    this.searchCreatorControl.setValue(this.selectedCreator?.name ?? "");
  }

  onAssignToFocus() {
    this.isAssignToEditing = true;
  }

  onAssignToBlur() {
    this.isAssignToEditing = false;
  }

  saveAssignTo() {
    this.task.assignUser = this.selectedAssignUser;
  }

  cancelAssignToEdit() {
    this.selectedAssignUser = this.task.assignUser;
    this.searchAssignUserControl.setValue(this.selectedAssignUser?.name ?? "");
  }

  saveTitle() {
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
    this.task.deadline = this.deadline;
  }

  cancelDeadline() {
    this.deadline = this.task.deadline;
  }

  // PROGRESS
  onProgressFocus() {
    this.isProgressEditing = true;
  }

  onProgressBlur() {
    this.isProgressEditing = false;
  }

  saveProgress() {
    this.task.progress = this.selectedProgress;
  }

  cancelProgress() {
    this.selectedProgress = this.task.progress;
  }

  onAddConnectedTaskInputChange() {
    const inputValue = this.searchConnectedTaskControl.value;
    if (!this.selectedConnectedTask || this.selectedConnectedTask.name !== inputValue) {
      this.selectedConnectedTask = null;
    }
  }

  onCreatorInputChange() {
    const inputValue = this.searchCreatorControl.value;
    if (!this.selectedCreator || this.selectedCreator.name !== inputValue) {
      this.selectedCreator = null;
    }
  }

  onAssignToInputChange() {
    const inputValue = this.searchAssignUserControl.value;
    if (!this.selectedAssignUser || this.selectedAssignUser.name !== inputValue) {
      this.selectedAssignUser = null;
    }
  }

  createNewSubTask() {
    let sub_task = new Task("");
    sub_task.name = this.new_sub_task_name;
    this.task.sub_tasks.push(sub_task);
    this.new_sub_task_name = "";
    this.isAddingSubTask = false;
  }

  addSelectedConnectedTask() {
    if (this.selectedConnectedTask !== null) {
      this.selectedConnectedTask.relation_to_parent = this.selectedConnectedTaskRelation;
      this.task.connected_tasks.push(this.selectedConnectedTask);
      this.searchConnectedTaskControl.setValue("");
      this.selectedConnectedTask = null;
    }
  }

  deleteSubTask(task: Task) {
    this.task.sub_tasks = this.task.sub_tasks.filter(st => st !== task);
  }

  deleteConnectedTask(task: Task) {
    this.task.connected_tasks = this.task.connected_tasks.filter(ct => ct !== task);
  }
}

class Task {
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
  progress: TaskProgress = TaskProgress.New;
  relation_to_parent: ConnectedTaskRelation = ConnectedTaskRelation.RelativeTo;


  constructor(name: string) {
    this.name = name;
  }
}

class User {
  name: string;
  constructor(name: string) {
    this.name = name;
  }
}

enum TaskProgress {
  New = "New",
  Proceeding = "Proceeding",
  Finished = "Finished"
}

enum ConnectedTaskRelation {
  Blocks = "blocks",
  BlockedBy = "Blocked by",
  RelativeTo = "Relative to"
}

