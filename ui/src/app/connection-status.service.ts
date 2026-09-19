import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {ConfigurationClient} from 'integration/client/configuration.client';
import {tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ConnectionStatusService {
  private connectedPlatformsSubject = new BehaviorSubject<{ [key: string]: boolean }>({});
  connectedPlatforms$: Observable<{ [key: string]: boolean }> = this.connectedPlatformsSubject.asObservable();

  constructor(private configClient: ConfigurationClient) {
    this.loadConnectionStatus();
  }

  loadConnectionStatus(): void {
    this.configClient.getConfig().pipe(
      tap(config => {
        const configData = config?.config || {};
        const connectedPlatforms = {
          'intervals': !!configData['intervals.api-key'] && !!configData['intervals.athlete-id'],
          'training-peaks': !!configData['training-peaks.auth-cookie'],
          'trainer-road': !!configData['trainer-road.auth-cookie'],
          'myfitnesspal': !!configData['mfp.session-cookie'] || !!configData['mfp.user-id'],
          'rouvy': !!configData['rouvy.email'],
          'wahoo': !!configData['wahoo.refresh-token']
        };
        this.connectedPlatformsSubject.next(connectedPlatforms);
      })
    ).subscribe();
  }
}
