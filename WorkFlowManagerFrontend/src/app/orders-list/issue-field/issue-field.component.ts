import { Component, Input } from '@angular/core';
import { FieldType } from '../../order/order.component';

@Component({
  selector: 'issue-field',
  templateUrl: './issue-field.component.html',
  styleUrl: './issue-field.component.css'
})
export class IssueFieldComponent {
  @Input() field: IssueFieldEditRest;
  @Input() editable?: boolean = false;

  setDate(event:any, field: IssueFieldEditRest) {
    const date = event.value;
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    field.value = `${year}-${month}-${day} 00:00:00`;
  }
  
}

export interface IssueFieldEditRest {
  organizationId: number,
  value: any,
  row: number,
  name: string;
  column: number;
  required: boolean;
  clientVisible: boolean;
  type: FieldType;
}
