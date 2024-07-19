import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { CustomValidators } from '../../validators/roleNameAvailable.validator';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { RoleDetailsService } from '../../services/organizations/role-settings/role-details.service';
import { ServiceResultHelper } from '../../services/utils/service-result-helper';

@Component({
  selector: 'app-role-create',
  templateUrl: './role-create.component.html',
  styleUrls: ['./role-create.component.css']
})
export class RoleCreateComponent {

  roleNameControl = new FormControl('', [Validators.required, CustomValidators.roleNameAvailable]);

  @Output() onCancel : EventEmitter<null> = new EventEmitter();
  @Output() onCreate : EventEmitter<string> = new EventEmitter();

  constructor(private service: RoleDetailsService,
    private serviceResultHelper: ServiceResultHelper,
    @Inject(MAT_DIALOG_DATA) public data: { organizationId: number}
  ) {}

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
    this.service.createRole(this.data.organizationId, valueStr).subscribe(result => {
      this.serviceResultHelper.handleServiceResult(result, "Role created successfully", "Errors occured");
      if (result.success) {
        this.onCreate.emit(valueStr);
      }
    });
  }
}
