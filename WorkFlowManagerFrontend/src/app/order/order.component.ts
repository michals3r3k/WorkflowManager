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
import { ServiceResult } from '../services/utils/service-result';
import { ResultToasterService } from '../services/result-toaster/result-toaster.service';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.css'],
})
export class OrderComponent implements OnInit {

  organizationId: number | null;
  statuses: string[];
  constStatuses: string[];
  categories: string[];
  newStatusName: string = "";
  newCategoryName: string = "";

  fields_column1: IssueFieldDefinitionRest[];
  fields_column2: IssueFieldDefinitionRest[];

  constructor(private route: ActivatedRoute, 
    private http: HttpRequestService,
    private resultToasterService: ResultToasterService,
    private serviceResultHelper: ServiceResultHelper) {
      this.statuses = [];
      this.constStatuses = [];
      this.categories = [];
      this.fields_column1 = [];
      this.fields_column2 = [];
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const organizationId = params.get("organizationId");
      this.organizationId = organizationId ? +organizationId : null;
      this._loadConfig();
    });
  }

  _loadConfig() {
    this.http.getGeneric<IssueDefinitionRest>(`api/organization/${this.organizationId}/issue-definition`).subscribe(issueDefinition => {
      this.statuses = issueDefinition.statuses;
      this.constStatuses = issueDefinition.constStatuses;
      this.categories = issueDefinition.categories;
      this.fields_column1 = issueDefinition.fields.filter(field => field.column == 1);
      this.fields_column2 = issueDefinition.fields.filter(field => field.column == 2);
    })
  }

  refresh() {
    this._loadConfig();
  }

  saveConfig() {
    const body: IssueDefinitionRest = {
      statuses: this.statuses,
      categories: this.categories,
      fields: [...this.fields_column1, ...this.fields_column2],
      constStatuses: []
    };
    this.http.postGeneric<ServiceResult>(`api/organization/${this.organizationId}/issue-definition/create`, body).subscribe(result => {
        this.serviceResultHelper.handleServiceResult(result, "Config saved successfully", "Errors occured");
        if (result.success) {
          this._loadConfig();
        }
      });
  }

  addCategory() {
    if (!this.newCategoryName) {
      this.resultToasterService.info("New category not given")
      return;
    }
    if (this.categories.includes(this.newCategoryName)) {
      this.resultToasterService.info("Duplicated category name");
      return;
    }
    this.categories.push(this.newCategoryName)
    this.newCategoryName = ""
  }

  addStatus() {
    if (!this.newStatusName) {
      this.resultToasterService.info("New status not given")
      return;
    }
    if (this.statuses.includes(this.newStatusName)) {
      this.resultToasterService.info("Duplicated status name");
      return;
    }

    this.statuses.push(this.newStatusName)
    this.newStatusName = ""
  }

  addNewField() {
    let count: number = 0;
    while (true) {
      if (!this.fields_column1.some(f => f.name === "new field" + (count === 0 ? "" : count.toString()))
        && !this.fields_column2.some(f => f.name === "new field" + (count === 0 ? "" : count.toString()))) {
        break;
      }
      count++;
    }
    this.fields_column1.push({
      definitionId: null,
      name: "new field" + (count === 0 ? "" : count.toString()),
      column: 1,
      type: FieldType.TEXT,
      required: false,
      clientVisible: false
    });
  }

  dropField(event: CdkDragDrop<IssueFieldDefinitionRest[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );
      for (let i = 0; i < event.container.data.length; i++) {
        event.container.data[i].column = event.container.data === this.fields_column1 ? 1 : 2;
      }
      for (let i = 0; i < event.previousContainer.data.length; i++) {
        event.previousContainer.data[i].column = event.previousContainer.data === this.fields_column1 ? 1 : 2;
      }
    }
  }
  
  isStatusToDelete(status: string) {
    return this.constStatuses.indexOf(status) === -1;
  }

  deleteFromList<T>(list: T[], element: T) {
    const index = list.indexOf(element);
    if(index > -1) {
      list.splice(index, 1);
    }
  }

}

interface IssueDefinitionRest {
  statuses: string[],
  categories: string[],
  fields: IssueFieldDefinitionRest[],
  constStatuses: string[],
}

export class IssueFieldDefinitionRest {
  definitionId: number | null;
  name: string;
  column: number;
  required: boolean;
  clientVisible: boolean;
  type: FieldType;
}

export enum FieldType {
  TEXT = "TEXT",
  DATE = "DATE",
  NUMBER = "NUMBER",
  FLAG = "FLAG"
}
