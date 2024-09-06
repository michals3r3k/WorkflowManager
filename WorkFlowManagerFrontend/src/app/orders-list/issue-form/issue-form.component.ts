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
  @Input() editMode: boolean = true;

  titleControl?: FormControl;
  descriptionControl?: FormControl;
  categoryControl?: FormControl;
  statusControl?: FormControl;

  fields_column1: IssueFieldEditRest[];
  fields_column2: IssueFieldEditRest[];

  issueStatusOptions: string[];
  issueCategoryOptions :string[];

  ngOnInit(): void {
    this.issueStatusOptions = this.issueForm.statusOptions;
    this.issueCategoryOptions = this.issueForm.categoryOptions;
    this.fields_column1 = this.issueForm.fields.filter(field => field.column == 1);
    this.fields_column2 = this.issueForm.fields.filter(field => field.column == 2);

    if(this.issueFormGroup) {
      [...this.fields_column1, ...this.fields_column2].forEach(field => {
        this.issueFormGroup?.addControl(field.key, new FormControl(field.value === null ? '' : field.value, field.required ? [Validators.required] : []));
      });

      this.titleControl = new FormControl(this.issueForm.title || '', [Validators.required]);
      this.titleControl.valueChanges.subscribe(value => {
        this.issueForm.title = value;
      });
      this.issueFormGroup.addControl('issuetitle', this.titleControl);

      this.descriptionControl = new FormControl(this.issueForm.description || '');
      this.descriptionControl.valueChanges.subscribe(value => {
        this.issueForm.description = value;
      });
      this.issueFormGroup.addControl('issueDescription', this.descriptionControl);

      this.categoryControl = new FormControl(this.issueForm.category || '', [Validators.required]);
      this.categoryControl.valueChanges.subscribe(value => {
        this.issueForm.category = value;
      });
      this.issueFormGroup.addControl('issueCategory', this.categoryControl);

      this.statusControl = new FormControl(this.issueForm.status || '', [Validators.required]);
      this.statusControl.valueChanges.subscribe(value => {
        this.issueForm.status = value;
      });
      this.issueFormGroup.addControl('issueStatus', this.statusControl);
    }
  }

}
