import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { OrderCreateComponent } from './order-create/order-create.component';
import { HttpRequestService } from '../services/http/http-request.service';
import { map, Observable } from 'rxjs';

@Component({
  selector: 'app-orders',
  templateUrl: './orders-list.component.html',
  styleUrls: ['./orders-list.component.css']
})
export class OrdersListComponent implements OnInit {
  @Input() organizationId: number;
  myIssues: IssueName[];
  clientsIssues: IssueName[];

  constructor(private dialog: MatDialog,
    private http: HttpRequestService) { }

  ngOnInit() {
    this._loadIssues();
  }

  _loadIssues() {
    this.http.getGeneric<IssueName[]>(`api/organization/${this.organizationId}/issue-names`).subscribe(issueNames => {
      this.myIssues = issueNames.filter(issue => issue.myIssue);
      this.clientsIssues = issueNames.filter(issue => !issue.myIssue);
    });
  }

  openAddOrderDialog() {
    const dialogRef = this.dialog.open(OrderCreateComponent, {
      data: {organizationId: this.organizationId}
    });
    dialogRef.componentInstance.afterSendReport.subscribe(() => {
      this._loadIssues();
      dialogRef.close();
    });
  }

}

interface IssueName {
  id: number,
  organizationName: string,
  myIssue: boolean,
}
