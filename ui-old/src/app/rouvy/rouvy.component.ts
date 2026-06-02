import {Component, OnInit} from '@angular/core';
import {
  RouvyCopyActivitiesToCalendarComponent
} from "./rouvy-copy-activities-to-calendar/rouvy-copy-activities-to-calendar.component";
import {MatExpansionModule} from "@angular/material/expansion";
import {NgIf} from "@angular/common";
import {ConfigurationClient} from "integration/client/configuration.client";
import {Platform} from "integration/platform";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatListModule} from "@angular/material/list";

@Component({
  selector: 'app-rouvy',
  standalone: true,
  imports: [
    RouvyCopyActivitiesToCalendarComponent,
    MatExpansionModule,
    NgIf,
    MatProgressBarModule,
    MatTooltipModule,
    MatListModule,

  ],
  templateUrl: './rouvy.component.html',
  styleUrl: './rouvy.component.scss'
})
export class RouvyComponent implements OnInit {
  platformInfo: any = undefined;

  readonly menuPlatform = Platform.ROUVY;

  constructor(
    private configurationClient: ConfigurationClient
  ) {
  }

  ngOnInit(): void {
    this.configurationClient.platformInfo(Platform.ROUVY.key).subscribe(value => {
      this.platformInfo = value
    })
  }
}
