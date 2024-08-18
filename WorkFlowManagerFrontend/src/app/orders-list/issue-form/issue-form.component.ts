import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IssueFieldEditRest } from '../issue-field/issue-field.component';
import { IssueFormRest } from '../../services/issue/issue-form.service';

@Component({
  selector: 'issue-form',
  templateUrl: './issue-form.component.html',
  styleUrl: './issue-form.component.css'
})
export class IssueFormComponent implements OnInit {
  @Input() issueForm: IssueFormRest;
  @Input() issueFormGroup?: FormGroup;

  titleControl?: FormControl;
  fields_column1: IssueFieldEditRest[];
  fields_column2: IssueFieldEditRest[];

  ngOnInit(): void {
    this.fields_column1 = this.issueForm.fields.filter(field => field.clientVisible && field.column == 1);
    this.fields_column2 = this.issueForm.fields.filter(field => field.clientVisible && field.column == 2);
    if(this.issueFormGroup) {
      [...this.fields_column1, ...this.fields_column2].forEach(field => {
        this.issueFormGroup?.addControl(field.key, new FormControl(field.value === null ? '' : field.value, field.required ? [Validators.required] : []));
      });
      this.titleControl = new FormControl(this.issueForm.title || '', [Validators.required]);
      this.titleControl.valueChanges.subscribe(value => {
        this.issueForm.title = value;
      });
      this.issueFormGroup.addControl('issuetitle', this.titleControl);
    }
  }

}
