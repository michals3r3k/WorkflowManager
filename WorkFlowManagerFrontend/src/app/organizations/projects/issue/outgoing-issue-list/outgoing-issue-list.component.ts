import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { IssueGroupRest, IssueRest, IssueService } from '../../../../services/issue/issue.service';
import { IssueDialogComponent } from '../../../../orders-list/organization-issue-dialog/issue-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-outgoing-issue-list',
  templateUrl: './outgoing-issue-list.component.html',
  styleUrl: './outgoing-issue-list.component.css'
})
export class OutgoingIssueListComponent {
  organizationId: number | null;
  projectId: number | null;

  //group$: Observable<IssueGroupRest>;
  issueGroup: IssueGroupRest;

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
      this.searchTextControl.valueChanges.pipe().subscribe(value =>{
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
      //this.group$ = this.issueService.getProjectOutgoingIssues(this.organizationId, this.projectId);
      this.issueService.getProjectOutgoingIssues(this.organizationId, this.projectId)
        .subscribe(issue => {
          this.issueGroup = issue;
          this.categoryOptions = ["All", ...new Set( issue.issueRestList.map(issue => issue.category))];
        this.statusOptions = ["All", ...new Set( issue.issueRestList.map(issue => issue.status))];
        });
    }
  }

  openDialog(issue: IssueRest) {
    this.dialog.open(IssueDialogComponent, {
      data: {
        forClient: true,
        organizationId: issue.organizationId,
        projectId: this.projectId,
        issueId: issue.id
      }
    });
  }

  filterIssues() {
    this.issueGroup.issueRestList.forEach(issue => {
      issue.hidden = false;

      if (this.filterSearchText || "".length > 0){
        if (!issue.title.toLowerCase().includes(this.filterSearchText.toLocaleLowerCase()) && !issue.id.toString().includes(this.filterSearchText)){
          issue.hidden = true;
        }
      }
      if (this.selectedFilterCategory != null) {
        console.log(this.selectedFilterCategory);
        console.log(typeof(this.selectedFilterCategory));
        if (issue.category != this.selectedFilterCategory){
          issue.hidden = true;
        }
      }
      if (this.selectedFilterStatus != null) {
        if (issue.status != this.selectedFilterStatus){
          issue.hidden = true;
        }
      }
    })
  }
}
