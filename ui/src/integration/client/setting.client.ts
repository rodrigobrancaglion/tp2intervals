import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class SettingClient {
  constructor(private http: HttpClient) {}

  syncPowerZones(): Observable<any> {
    return this.http.post('/api/settings/power/sync', {});
  }

  getSchedulerStatus(): Observable<{enabled: boolean}> {
    return this.http.get<{enabled: boolean}>('/api/settings/power/scheduler');
  }

  toggleScheduler(enabled: boolean): Observable<{enabled: boolean}> {
    return this.http.post<{enabled: boolean}>('/api/settings/power/scheduler', {enabled});
  }
}
