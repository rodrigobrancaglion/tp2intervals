import {Component, OnInit} from '@angular/core';
import {ConfigurationClient} from "integration/client/configuration.client";
import {NgClass, NgIf} from "@angular/common";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [NgIf, NgClass, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  inProgress = false;
  connectedPlatforms: { [key: string]: boolean } = {
    'intervals': false,
    'training-peaks': false,
    'trainer-road': false,
    'myfitnesspal': false,
    'rouvy': false,
    'wahoo': false
  };

  platforms = [
    { id: 'intervals', name: 'Intervals.icu', desc: 'Unified workout database and training calendar target platform.', url: '/training-peaks' },
    { id: 'training-peaks', name: 'TrainingPeaks', desc: 'Synchronize planned workouts, full plans, events, wellness metrics, and power zones.', url: '/training-peaks' },
    //{ id: 'wahoo', name: 'Wahoo Fitness', desc: 'Sync workout logs, routes, and power zones directly with your Wahoo cloud.', url: '/wahoo' },
    { id: 'wahoo', name: 'Wahoo Fitness', desc: 'Sync power zones directly with your Wahoo cloud.', url: '/training-peaks' },
    { id: 'rouvy', name: 'Rouvy', desc: 'Beta: Import and synchronize indoor cycling activities.', url: '/rouvy' },
    { id: 'trainer-road', name: 'TrainerRoad', desc: 'Export plans and libraries directly to your Intervals calendar.', url: '/trainer-road' },
    { id: 'myfitnesspal', name: 'MyFitnessPal', desc: 'Sync nutritional wellness metrics and weight logs.', url: '/myfitnesspal' }
  ];

  constructor(private configClient: ConfigurationClient) {}

  ngOnInit(): void {
    this.inProgress = true;
    this.configClient.getConfig().subscribe({
      next: (config) => {
        const configData = config?.config || {};
        this.connectedPlatforms['intervals'] = !!configData['intervals.api-key'] && !!configData['intervals.athlete-id'];
        this.connectedPlatforms['training-peaks'] = !!configData['training-peaks.auth-cookie'];
        this.connectedPlatforms['trainer-road'] = !!configData['trainer-road.auth-cookie'];
        this.connectedPlatforms['myfitnesspal'] = !!configData['mfp.session-cookie'] || !!configData['mfp.user-id'];
        this.connectedPlatforms['rouvy'] = !!configData['rouvy.email'];
        this.connectedPlatforms['wahoo'] = !!configData['wahoo.refresh-token'];
        this.inProgress = false;
      },
      error: () => {
        this.inProgress = false;
      }
    });
  }
}
