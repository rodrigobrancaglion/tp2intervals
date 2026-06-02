import {Component, OnInit} from '@angular/core';
import {
  TpCopyCalendarToCalendarComponent
} from "app/training-peaks/tp-copy-calendar-to-calendar/tp-copy-calendar-to-calendar.component";
import {
  TpCopyLibraryContainerComponent
} from "app/training-peaks/tp-copy-library-container/tp-copy-library-container.component";
import {
  TpCopyCalendarToLibraryComponent
} from "app/training-peaks/tp-copy-calendar-to-library/tp-copy-calendar-to-library.component";
import {
  TpCopyWellnessToCalendarComponent
} from "app/training-peaks/tp-copy-wellness-to-calendar/tp-copy-wellness-to-calendar.component";
import {
  TpCopyActivitiesToCalendarComponent
} from "app/training-peaks/tp-copy-activities-to-calendar/tp-copy-activities-to-calendar.component";
import {
  TpCopyEventsToCalendarComponent
} from "app/training-peaks/tp-copy-events-to-calendar/tp-copy-events-to-calendar.component";
import {TpSettingPowerComponent} from "app/training-peaks/tp-setting-power/tp-setting-power.component";
import {MatExpansionModule} from "@angular/material/expansion";
import {NgIf} from "@angular/common";
import {ConfigurationClient} from "integration/client/configuration.client";
import {Platform} from "integration/platform";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatListModule} from "@angular/material/list";

@Component({
  selector: 'app-training-peaks',
  standalone: true,
  imports: [
    TpCopyCalendarToCalendarComponent,
    TpCopyLibraryContainerComponent,
    TpCopyCalendarToLibraryComponent,
    TpCopyWellnessToCalendarComponent,
    TpCopyActivitiesToCalendarComponent,
    TpCopyEventsToCalendarComponent,
    TpSettingPowerComponent,
    MatExpansionModule,
    NgIf,
    MatProgressBarModule,
    MatTooltipModule,
    MatListModule,
  ],
  templateUrl: './training-peaks.component.html',
  styleUrl: './training-peaks.component.scss'
})
export class TrainingPeaksComponent implements OnInit {
  platformInfo: any = undefined;

  readonly menuPlatform = Platform.TRAINING_PEAKS;

  constructor(
    private configurationClient: ConfigurationClient
  ) {
  }

  ngOnInit(): void {
    this.configurationClient.platformInfo(Platform.TRAINING_PEAKS.key).subscribe(value => {
      this.platformInfo = value
    })
  }
}
