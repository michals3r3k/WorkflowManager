import { Component, EventEmitter, Input, Output } from '@angular/core';
import { IssueFieldDefinitionRest } from '../order.component';

@Component({
  selector: 'app-field-definition-edit',
  templateUrl: './field-definition-edit.component.html',
  styleUrl: './field-definition-edit.component.css'
})
export class FieldDefinitionEditComponent {
  @Input() field: IssueFieldDefinitionRest;
  @Input() statuses: any[];
  @Output() delete = new EventEmitter<void>();

  deleteField() {
    this.delete.emit();
  }
}
