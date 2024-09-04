import { Component, Input, OnInit } from '@angular/core';
import { FieldType } from '../../order/order.component';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'issue-field',
  templateUrl: './issue-field.component.html',
  styleUrl: './issue-field.component.css'
})
export class IssueFieldComponent implements OnInit {
  @Input() field: IssueFieldEditRest;
  @Input() issueFormGroup?: FormGroup;
  @Input() editMode: boolean;

  valueControl: FormControl;

  ngOnInit() {
    if(!this.issueFormGroup) {
      return;
    }
    this.valueControl = this.issueFormGroup.get(this.field.key) as FormControl;
    this.valueControl.valueChanges.subscribe(value => {
      if(this.field.type === FieldType.DATE) {
        this.field.value = this.getDateString(value);
      }
      else {
        this.field.value = value; 
      }
    });
  }

  getDateString(date: any): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day} 00:00:00`;
  }
  
}

export interface IssueFieldEditRest {
  organizationId: number,
  row: number,
  column: number,
  key: string,
  value: any,
  name: string,
  required: boolean,
  clientVisible: boolean,
  type: FieldType
}
