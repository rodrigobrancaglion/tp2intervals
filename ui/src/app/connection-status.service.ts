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
        const isIcuConfigured = !!configData['intervals.api-key'] && !!configData['intervals.athlete-id'];
        const isTpConfigured = !!configData['training-peaks.auth-cookie'];

        const connectedPlatforms = {
          'intervals': isIcuConfigured,
          'training-peaks': isIcuConfigured && isTpConfigured,
          'trainer-road': isIcuConfigured && !!configData['trainer-road.auth-cookie'],
          'myfitnesspal': isIcuConfigured && (!!configData['mfp.session-cookie'] || !!configData['mfp.user-id']),
          'rouvy': isIcuConfigured && !!configData['rouvy.email'],
          'wahoo': isIcuConfigured && !!configData['wahoo.refresh-token']
        };
        this.connectedPlatformsSubject.next(connectedPlatforms);
      })
    ).subscribe();
  }
}
