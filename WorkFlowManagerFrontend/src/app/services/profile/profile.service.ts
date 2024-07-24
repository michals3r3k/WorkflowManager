import { Injectable } from '@angular/core';
import { HttpRequestService } from '../http/http-request.service';
import { map, Observable, of } from 'rxjs';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { HttpClient } from '@angular/common/http';
import { ServiceResult } from '../utils/service-result';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  constructor(private httpService: HttpRequestService,
    private httpClient: HttpClient,
    private sanitizer: DomSanitizer) { }
    
  saveData(profileEditData: ProfileEdit): Observable<ServiceResult> {
    return this.httpService.postGeneric<ServiceResult>("api/profile/save", profileEditData);
  }

  getData(): Observable<ProfileEdit> {
    return this.httpService.getGeneric<ProfileEdit>("api/profile");
  }

  uploadImg(formData: FormData): Observable<SafeUrl | null> {
    const headers = this.httpService.getHttpHeaders()
    if(headers) {
      return this.httpClient.post("http://localhost:8080/api/profile/img/upload", formData, {headers: headers, responseType: 'blob'}).pipe(
        map(blob => {
          const objectURL = URL.createObjectURL(blob);
          return this.sanitizer.bypassSecurityTrustUrl(objectURL)
        }));
    }
    return of(null);
  }

  saveImg(formData: FormData): Observable<ServiceResult> {
    return this.httpService.postGeneric<ServiceResult>("api/profile/img/save", formData);
  }

  getImg(): Observable<SafeUrl | null> {
    const headers = this.httpService.getHttpHeaders()
    if(headers) {
      return this.httpClient.get("http://localhost:8080/api/profile/img", {headers: headers, responseType: 'blob'}).pipe(
        map(blob => {
          const objectURL = URL.createObjectURL(blob);
          return this.sanitizer.bypassSecurityTrustUrl(objectURL)
        }));
    }
    return of(null);
  }

}

export interface ProfileEdit {
  firstName: string | null,
  secondName: string | null
}
