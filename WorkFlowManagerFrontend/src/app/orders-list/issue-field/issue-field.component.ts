import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { FieldType } from '../../order/order.component';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';

@Component({
  selector: 'issue-field',
  templateUrl: './issue-field.component.html',
  styleUrl: './issue-field.component.css'
})
export class IssueFieldComponent implements OnChanges {
  @Input() field: IssueFieldEditRest;
  @Input() issueFormGroup?: FormGroup;
  @Input() editMode: boolean;

  valueControl: FormControl | null;
  subscription?: Subscription;

  ngOnChanges(changes: SimpleChanges): void {
    if(changes["field"] || changes["issueFormGroup"]) {  
      this.valueControl = null;
      this.subscription?.unsubscribe();
      this._init();
    }
  }

  _init() {
    if(!this.issueFormGroup) {
      return;
    }
    this.valueControl = this.issueFormGroup.get(this.field.key) as FormControl;
    if(this.field.type === FieldType.DATE && this.field.value) {
      this.valueControl.setValue(new Date(this.field.value));
    }
    this.subscription = this.valueControl.valueChanges.subscribe(value => {
      if(this.field.type === FieldType.DATE) {
        this.field.value = this.getDateString(value);
      }
      else {
        this.field.value = value; 
      }
    });
  }

  getDateString(date: Date): string {
    if(!date) {
      return "";
    }
    return date.toISOString();
  }

  get valueString(): string {
    if(this.field.type == FieldType.FLAG) {
      return this.field.value ? "true" : "false";
    }
    if(!this.field.value) {
      return "";
    }
    if(this.field.type === FieldType.DATE) {
      const date = new Date(this.field.value);
      return `${date.getMonth() + 1}/${date.getDate()}/${date.getFullYear()}`;
    }
    return this.field.value;
  }
  
}

export interface IssueFieldEditRest {
  definitionId: number,
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
