import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class ActivityClient {

  constructor(private httpClient: HttpClient) {
  }

  copyActivities(startDate, endDate, types, platformDirection): Observable<any> {
    return this.httpClient
      .post(`/api/activities/copy`, {startDate, endDate, types, ...platformDirection})
  }

  copyCalendarToCalendar(startDate, endDate, types, platformDirection): Observable<any> {
    return this.httpClient
      .post(`/api/activities/copy-calendar-to-calendar`, {startDate, endDate, types, ...platformDirection})
  }

  scheduleCopyCalendarToCalendar(startDate, endDate, types, platformDirection): Observable<any> {
    return this.httpClient
      .post(`/api/activities/copy-calendar-to-calendar/schedule`, {
        startDate,
        endDate,
        types,
        ...platformDirection
      })
  }

  getScheduleRequests(): Observable<any> {
    return this.httpClient.get(`/api/activities/copy-calendar-to-calendar/schedule`)
  }

  deleteScheduleRequest(id: any) {
    return this.httpClient.delete(`/api/activities/copy-calendar-to-calendar/schedule/${id}`)
  }

}
