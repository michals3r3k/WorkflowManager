import { Injectable } from '@angular/core';
import { ResultToasterService } from '../result-toaster/result-toaster.service';
import { ServiceResult } from './service-result';

@Injectable({
  providedIn: 'root'
})
export class ServiceResultHelper {
  constructor(private toaster : ResultToasterService) { }

  public handleServiceResult(serviceResult: ServiceResult, 
    successMessage: string, errorMessage: string) {
    if(serviceResult.success) {
        this.toaster.success(successMessage);
    } 
    else {
        this.toaster.error(`${errorMessage} ${serviceResult.errors}`);
    }
  }

}
