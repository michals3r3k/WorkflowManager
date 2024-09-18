import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { IssueGroupRest, IssueRest, IssueService } from '../../../../services/issue/issue.service';
import { IssueDialogComponent } from '../../../../orders-list/organization-issue-dialog/issue-dialog.component';
import { debounceTime, Observable } from 'rxjs';
import { ReactiveFormsModule, FormControl } from '@angular/forms';

@Component({
  selector: 'app-incomming-issue-list',
  templateUrl: './incomming-issue-list.component.html',
  styleUrl: './incomming-issue-list.component.css'
})
export class IncommingIssueListComponent implements OnInit {
  organizationId: number | null;
  projectId: number | null;

  //issueGroups$: Observable<IssueGroupRest[]>;
  issueGroups: IssueGroupRest[] = [];

  // FILTERING
  searchTextControl: FormControl = new FormControl();
  categoryOptions: string[] = [];
  statusOptions: string[] = [];
  filterSearchText: string = "";
  _selectedFilterCategory: string = "All";
  get selectedFilterCategory(): string { return this._selectedFilterCategory; }
  set selectedFilterCategory(value: string) {
    this._selectedFilterCategory = value;
    this.filterIssues();
  }
  _selectedFilterStatus: string = "All";
  get selectedFilterStatus(): string { return this._selectedFilterStatus; }
  set selectedFilterStatus(value: string) {
    this._selectedFilterStatus = value;
    this.filterIssues();
  }


  constructor(private dialog: MatDialog,
    private route: ActivatedRoute,
    private issueService: IssueService) 
    {
      this.searchTextControl.valueChanges.pipe(debounceTime(400)).subscribe(value =>{
        this.filterSearchText = value;
        this.filterIssues();
      })
    }

    ngOnInit() {
      this.route.paramMap.subscribe(params => {
        const projectId = params.get("projectId");
        const organizationId = params.get("organizationId");
        const taskId = params.get("taskId");
        this.projectId = projectId == null ? null : +projectId;
        this.organizationId = organizationId == null ? null : +organizationId;
        this._loadIssues();
      })    
    }

  _loadIssues() {
    if(this.organizationId && this.projectId) {
      this.issueService.getProjectIncomingIssues(this.organizationId, this.projectId).subscribe(issues => {
        this.issueGroups = issues;
        this.categoryOptions = ["All", ...new Set( issues.flatMap(group => group.issueRestList.map(issue => issue.category)))];
        this.statusOptions = ["All", ...new Set( issues.flatMap(group => group.issueRestList.map(issue => issue.status)))];
      });
    }
  }

  openDialog(issue: IssueRest) {
    this.dialog.open(IssueDialogComponent, {
      data: {
        forClient: false,
        organizationId: this.organizationId,
        projectId: this.projectId,
        issueId: issue.id
      }
    });
  }

  filterIssues() {
    this.issueGroups.forEach(group => {
      group.issueRestList.forEach(issue => {
        issue.hidden = false;

        if (this.filterSearchText || "".length > 0){
          if (this.filterSearchText.length !== 0 && !issue.title.toLowerCase().includes(this.filterSearchText.toLocaleLowerCase()) && !issue.id.toString().includes(this.filterSearchText)){
            issue.hidden = true;
          }
        }
        if (this.selectedFilterCategory !== "All") {
          if (issue.category !== this.selectedFilterCategory){
            issue.hidden = true;
          }
        }
        if (this.selectedFilterStatus !== "All") {
          if (issue.status !== this.selectedFilterStatus){
            issue.hidden = true;
          }
        }
      })
    })
  }

}
