import { Component } from '@angular/core';
import {
  CdkDragDrop,
  CdkDrag,
  CdkDropList,
  CdkDropListGroup,
  moveItemInArray,
  transferArrayItem,
} from '@angular/cdk/drag-drop';

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
}

interface TaskGroup {
  groupName: string,
  tasks: string[]
}
