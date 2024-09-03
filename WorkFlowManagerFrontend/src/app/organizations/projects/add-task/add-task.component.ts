import { Component, ElementRef, EventEmitter, HostListener, Input, OnInit, Output, Renderer2, ViewChild } from '@angular/core';
import { TaskGroup } from '../project-details/project-details.component';

@Component({
  selector: 'app-add-task',
  templateUrl: './add-task.component.html',
  styleUrls: ['./add-task.component.css']
})
export class AddTaskComponent implements OnInit {
  @ViewChild('taskNameInput') inputField: ElementRef | undefined;
  @Input() group: TaskGroup | null;

  @Output() addTaskClicked = new EventEmitter<string>();

  isFocus = false;
  task_name = "";

  constructor(private elRef: ElementRef, private renderer: Renderer2) { }

  ngOnInit() {
  }

  enableEditing() {
    this.isFocus = true;
    setTimeout(() => {
      if (this.inputField) {
        this.inputField.nativeElement.focus();
      }
    }, 0);
  }

  addTask() {
    const eventData = { task: this.task_name, group: this.group };
    this.addTaskClicked.emit(this.task_name);
    this.task_name = "";
    this.isFocus = false;
  }

  // Method to handle clicks outside the component
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const clickedElement = event.target as HTMLElement;
    if (!this.elRef.nativeElement.contains(clickedElement)) {
      this.isFocus = false;
    }
  }

  // Prevent clicks inside the component from being considered as outside clicks
  onInsideClick(event: Event) {
    event.stopPropagation();
  }
}
