import { Component, EventEmitter, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Observable, debounceTime, filter, map, startWith, switchMap } from 'rxjs';
import { HttpRequestService } from '../../services/http/http-request.service';
import { ResultToasterService } from '../../services/result-toaster/result-toaster.service';

@Component({
  selector: 'app-organization-member-picker',
  templateUrl: './organization-member-picker.component.html',
  styleUrl: './organization-member-picker.component.css'
})
export class OrganizationMemberPickerComponent {
  userEmailControl = new FormControl();
  filteredOptions$: Observable<any[]>;  

  @Output() onUserSelected : EventEmitter<number> = new EventEmitter();

  constructor(private http: HttpRequestService,
    private resultToaster: ResultToasterService) {
    this.filteredOptions$ = this.userEmailControl.valueChanges
      .pipe(
        startWith(''),
        debounceTime(400),
        switchMap(() => this.loadUsers()));
  }

  addMember() {
    let email = this.userEmailControl.value;
    let sub = this.filteredOptions$
      .pipe(map(users => users
        .filter(user => user.name === email)
        .map(user => user.id)))
      .subscribe(userIds => {
        sub.unsubscribe();
        let userId : number | null = userIds[0] || null;
        if(!userId) {
          this.resultToaster.error("User isn't in the list.");
          return;
        }
        this.onUserSelected.emit(userId);
      });
  }

  private loadUsers(): Observable<any[]> {
    return this.http.get("api/users/like/" + (this.userEmailControl.value || null));
  }  

}
