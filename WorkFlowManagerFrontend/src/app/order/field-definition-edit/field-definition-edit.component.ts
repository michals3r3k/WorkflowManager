import { Component, Input } from '@angular/core';
import { OrderFieldModel } from '../order.component';

@Component({
  selector: 'app-field-definition-edit',
  templateUrl: './field-definition-edit.component.html',
  styleUrl: './field-definition-edit.component.css'
})
export class FieldDefinitionEditComponent {
  @Input() field: OrderFieldModel;
  @Input() statuses: any[];
}
