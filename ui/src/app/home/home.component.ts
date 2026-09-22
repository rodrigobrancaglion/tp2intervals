import {Component, OnInit} from '@angular/core';
import {AsyncPipe, NgClass, NgIf} from "@angular/common";
import {RouterLink} from "@angular/router";
import {ConnectionStatusService} from "app/connection-status.service";
import {Observable} from "rxjs";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [NgIf, NgClass, RouterLink, AsyncPipe],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  inProgress = false;
  connectedPlatforms$: Observable<{ [key: string]: boolean }>;

  platforms = [
    {
      id: 'training-peaks',
      name: 'TrainingPeaks ➔ Intervals.icu',
      desc: 'Synchronize planned workouts, full training plans, events, wellness metrics, and power zones between TrainingPeaks and Intervals.icu.',
      url: '/training-peaks'
    },
    { id: 'wahoo', name: 'Wahoo Fitness', desc: 'Sync power zones directly with your Wahoo cloud.', url: '/training-peaks' },
    { id: 'rouvy', name: 'Rouvy', desc: 'Beta: Import and synchronize indoor cycling activities.', url: '/rouvy' },
    { id: 'trainer-road', name: 'TrainerRoad', desc: 'Export plans and libraries directly to your Intervals calendar.', url: '/trainer-road' },
    { id: 'myfitnesspal', name: 'MyFitnessPal', desc: 'Sync nutritional wellness metrics and weight logs.', url: '/myfitnesspal' }
  ];

  constructor(private connectionStatusService: ConnectionStatusService) {}

  ngOnInit(): void {
    this.connectedPlatforms$ = this.connectionStatusService.connectedPlatforms$;
  }
}
