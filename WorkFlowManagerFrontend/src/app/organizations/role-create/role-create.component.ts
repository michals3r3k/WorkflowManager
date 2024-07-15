import { Component, EventEmitter, Inject, Input, OnInit, Output } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { CustomValidators } from '../../validators/roleNameAvailable.validator';
import { HttpRequestService } from '../../services/http/http-request.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-role-create',
  templateUrl: './role-create.component.html',
  styleUrls: ['./role-create.component.css']
})
export class RoleCreateComponent implements OnInit {

  roleNameControl = new FormControl('', [Validators.required, CustomValidators.roleNameAvailable]);

  @Output() onCancel : EventEmitter<null> = new EventEmitter();
  @Output() onCreate : EventEmitter<null> = new EventEmitter();

  constructor(private httpService: HttpRequestService,
    @Inject(MAT_DIALOG_DATA) public data: { organizationId: number}
  ) {}

  ngOnInit() {
  }

  cancel() {
    this.onCancel.emit();
  }

  create() {
    alert(this.data.organizationId)
    var valueStr = ""
    const value = this.roleNameControl.value;
    if (value !== null && value !== undefined) {
      valueStr = value.toString();
    }
    else {
      alert("Error occured when trying to read role name.")
    }
    this.httpService.post("api/organization/" + this.data.organizationId + "/role/add", new RoleAddModel(valueStr)).subscribe(res => {
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
  role: string;

  constructor(role: string) {
    this.role = role;
  }
}
