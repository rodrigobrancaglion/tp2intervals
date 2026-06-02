import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class ActivityClient {

  constructor(private httpClient: HttpClient) {
  }

  copyCalendarToCalendar(startDate, endDate, types, platformDirection): Observable<any> {
    return this.httpClient
      .post(`/api/activity/copy-calendar-to-calendar`, {startDate, endDate, types, ...platformDirection})
  }

  scheduleCopyCalendarToCalendar(startDate, endDate, types, platformDirection, currentPlatform): Observable<any> {
    return this.httpClient
      .post(`/api/activity/copy-calendar-to-calendar/schedule`,
        {startDate, endDate, types, ...platformDirection},
        {params: {platform: currentPlatform}}
      )
  }

  getScheduleRequests(platform: string): Observable<any> {
    return this.httpClient.get(`/api/activity/copy-calendar-to-calendar/schedule`, {params: {platform}})
  }

  deleteScheduleRequest(id: any) {
    return this.httpClient.delete(`/api/activity/copy-calendar-to-calendar/schedule/${id}`)
  }

}
