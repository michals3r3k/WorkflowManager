import { Pipe, PipeTransform } from '@angular/core';
import { HttpRequestService } from '../services/http/http-request.service';
import { catchError, map, Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Pipe({
  name: 'userImg'
})
export class UserImgPipe implements PipeTransform {
  
  constructor(private httpClient: HttpClient,
    private http: HttpRequestService) {}

  transform(userId: number | null): Observable<string | null> {
    if (!userId) {
      return of(null);
    }
    const headers = this.http.getHttpHeaders()
    if(!headers) {
      return of(null);;
    }
    return this.httpClient.get(`http://localhost:8080/api/chat/user/${userId}/img`, {headers: headers, responseType: 'blob'})
      .pipe(
        map(blob => !!blob.size ? URL.createObjectURL(blob) : null),
        catchError(() => of(null))
      );
  }

}
