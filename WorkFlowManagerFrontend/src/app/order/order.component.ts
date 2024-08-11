import { Component, OnInit } from '@angular/core';
import {
  CdkDragDrop,
  CdkDrag,
  CdkDropList,
  CdkDropListGroup,
  moveItemInArray,
  transferArrayItem,
} from '@angular/cdk/drag-drop';
import { ActivatedRoute } from '@angular/router';
import { HttpRequestService } from '../services/http/http-request.service';
import { ServiceResultHelper } from '../services/utils/service-result-helper';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.css'],
})
export class OrderComponent implements OnInit {

  organizationId: string | null;
  statuses = ["NEW", "PROCESSING", "FINISHED"];
  categories = ["ERROR", "APP", "SERVICE"];
  newStatusName: string = "";
  newCategoryName: string = "";

  order_data: OrderFieldModel[] = [
    {
      name: "field 1",
      column: 1,
      type: FieldType.TEXT,
      required: false,
      clientVisible: false
    },
    {
      name: "field 2",
      column: 1,
      type: FieldType.NUMBER,
      required: false,
      clientVisible: false
    },
    {
      name: "field 3",
      column: 2,
      type: FieldType.FLAG,
      required: false,
      clientVisible: false
    },
    {
      name: "field 4",
      column: 2,
      type: FieldType.FLAG,
      required: false,
      clientVisible: false
    },
    {
      name: "field 5",
      column: 1,
      type: FieldType.FLAG,
      required: false,
      clientVisible: false
    },
    {
      name: "field 6",
      column: 1,
      type: FieldType.FLAG,
      required: false,
      clientVisible: false
    }
  ];

  fields_column1: OrderFieldModel[] = [];
  fields_column2: OrderFieldModel[] = [];

  getColumn1Fields() {
    return this.order_data.filter(field => field.column == 1)
  }

  getColumn2Fields() {
    return this.order_data.filter(field => field.column == 2)
  }

  constructor(private route: ActivatedRoute, private http: HttpRequestService,
    private serviceResultHelper: ServiceResultHelper) { }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.organizationId = params.get("organizationId");
    });
    this.fields_column1 = this.getColumn1Fields();
    this.fields_column2 = this.getColumn2Fields();
  }

  saveConfig() {
    this.http.post(`api/organization/${this.organizationId}/issue-definition/create`, 
      [...this.fields_column1, ...this.fields_column2]).subscribe(result => {
        this.serviceResultHelper.handleServiceResult(result, "Config saved successfully", "Errors occured");
      });
  }

  addCategory() {
    if (!this.newCategoryName) {
      // Snackbar with error
      return;
    }
    if (this.categories.includes(this.newCategoryName)) {
      // Snackbar with error
      return;
    }

    this.categories.push(this.newCategoryName)
    this.newCategoryName = ""
  }

  addStatus() {
    if (!this.newStatusName) {
      // Snackbar with error
      return;
    }
    if (this.categories.includes(this.newStatusName)) {
      // Snackbar with error
      return;
    }

    this.statuses.push(this.newStatusName)
    this.newStatusName = ""
  }

  addNewField() {
    let count: number = 0;
    while (true) {
      if (!this.fields_column1.some(f => f.name === "new field" + (count === 0 ? "" : count.toString()))
        && !this.fields_column2.some(f => f.name === "new field" + (count === 0 ? "" : count.toString()))){
          break;
        }
        count++;
    }
    this.fields_column1.push({
      name: "new field" + (count === 0 ? "" : count.toString()), 
      column: 1, 
      type: FieldType.TEXT,
      required: false, 
      clientVisible: false
    });
  }

  dropField(event: CdkDragDrop<OrderFieldModel[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
      // for (let i = 0; i < event.container.data.length; i++){
      //   event.container.data[i].position = i;
      // }
      // event.container.data.forEach(f => {
      //   console.log(f.name + " " + f.position);
      // });

    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );
      for (let i = 0; i < event.container.data.length; i++){
        // event.container.data[i].position = i;
        event.container.data[i].column = event.container.data === this.fields_column1 ? 1 : 2;
      }
      for (let i = 0; i < event.previousContainer.data.length; i++){
        // event.previousContainer.data[i].position = i;
        event.previousContainer.data[i].column = event.previousContainer.data === this.fields_column1 ? 1 : 2;
      }

      // event.container.data.forEach(f => {
      //   console.log(f.name + " " + f.position + " " + f.column);
      // });
      // event.previousContainer.data.forEach(f => {
      //   console.log(f.name + " " + f.position + " " + f.column);
      // });
    }
  }
}

export class OrderFieldModel  { 
  name: string;
  column: number;
  required: boolean;
  clientVisible: boolean;
  type: FieldType;
}

enum FieldType {
  TEXT = "TEXT",
  DATE = "DATE",
  NUMBER = "NUMBER",
  FLAG = "FLAG"
}
