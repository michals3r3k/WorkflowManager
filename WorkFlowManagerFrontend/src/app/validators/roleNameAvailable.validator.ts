import { AbstractControl, AsyncValidatorFn, FormControl, ValidationErrors } from "@angular/forms";
import { HttpRequestService } from "../services/http/http-request.service";
import { Injectable } from "@angular/core";
import { Observable, debounceTime, distinctUntilChanged, first, map, of, switchMap, timer } from "rxjs";

@Injectable({
    providedIn: 'root'
  })
export class CustomValidators {

    constructor (private httpService: HttpRequestService) {
    }

    roleNameAvailable(organizationId: number): AsyncValidatorFn {
        return control => control.valueChanges
            .pipe(
                debounceTime(400),
                distinctUntilChanged(),
                switchMap(value => this.httpService.get(`api/organization/${organizationId}/role/${value}/available`)),
                map((result: boolean) => (result ? null : {roleNameAvailable: "Role name is taken"})),
                first());
      }
}