import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { IssueFieldEditRest } from '../issue-field/issue-field.component';
import { HttpRequestService } from '../../services/http/http-request.service';
import { debounceTime, Observable, of, startWith, switchMap } from 'rxjs';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-client-order-dialog',
  templateUrl: './client-order-dialog.component.html',
  styleUrl: './client-order-dialog.component.css'
})
export class ClientOrderDialogComponent implements OnInit{
  organizationId: number | null;
  issueId: number;
  issueDetailsUrl: string;
  issue$: Observable<IssueDetailsRest>;

  projectNameControl: FormControl = new FormControl();
  projectOptions: ProjectOptionRest[];
  projectOptions$: Observable<ProjectOptionRest[]>;

  existingProject: boolean = true;
  
  constructor(@Inject(MAT_DIALOG_DATA) private data: {organizationId?: number
    issueDetailsUrl: string
  },
    private http: HttpRequestService) {
    this.organizationId = data.organizationId || null;
    this.issueDetailsUrl = data.issueDetailsUrl;

    if(this.organizationId) {
      this.projectOptions = [{id: 1, name: "bbb"}, {id: 2, name: "ccc"}]
      this.projectOptions$ = this.projectNameControl.valueChanges
      .pipe(
        startWith(''),
        debounceTime(400),
        switchMap(() => {
          const name = this.projectNameControl.value;
          return of(this.projectOptions.filter(option => option.name.indexOf(name) >= 0));
        })
      );
    }
  }

  ngOnInit(): void {
    this.issue$ = this.http.getGeneric<IssueDetailsRest>(this.issueDetailsUrl);
  }

}

interface ProjectOptionRest {
  id: number;
  name: string;
}

interface IssueDetailsRest {
  id: number,
  organizationName: string,
  col1Fields: IssueFieldEditRest[],
  col2Fields: IssueFieldEditRest[]
}
