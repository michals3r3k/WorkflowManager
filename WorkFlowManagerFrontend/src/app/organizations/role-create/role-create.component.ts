import { Component, EventEmitter, Inject, Input, OnInit, Output } from '@angular/core';
import { Form, FormControl, ValidationErrors, Validators, AsyncValidatorFn, AbstractControl } from '@angular/forms';
import { CustomValidators } from '../../validators/roleNameAvailable.validator';
import { HttpRequestService } from '../../services/http/http-request.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Observable, of } from 'rxjs';

@Component({
  selector: 'app-role-create',
  templateUrl: './role-create.component.html',
  styleUrls: ['./role-create.component.css']
})
export class RoleCreateComponent implements OnInit {

  roleNameControl = new FormControl('', [Validators.required], [this.cv.roleNameAvailable(this.data.organizationId)]);
  
  @Output() onCancel : EventEmitter<null> = new EventEmitter();
  @Output() onCreate : EventEmitter<null> = new EventEmitter();

  constructor(private httpService: HttpRequestService,
    private cv: CustomValidators,
    @Inject(MAT_DIALOG_DATA) public data: { organizationId: number}
  ) {
  }

  ngOnInit() {
  }

  cancel() {
    this.onCancel.emit();
  }

  create() {
    var valueStr = ""
    const value = this.roleNameControl.value;
    if (value !== null && value !== undefined) {
      valueStr = value.toString();
    }
    else {
      alert("Error occured when trying to read role name.")
    }
    this.httpService.post("api/organization/role/add", new RoleAddModel(this.data.organizationId, valueStr)).subscribe(res => {
      if (res.success) {
        this.onCreate.emit(res);
      }
      else {
        alert(res.errors);
      }
    });
  }
}

class RoleAddModel {
  organizationId: number;
  role: string;

  constructor(id: number, role: string) {
    this.organizationId = id;
    this.role = role;
  }
}
