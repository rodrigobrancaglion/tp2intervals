import {Component, OnInit} from '@angular/core';
import {
  MfpCopyWellnessToCalendarComponent
} from "app/myfitnesspal/mfp-copy-wellness-to-calendar/mfp-copy-wellness-to-calendar.component";
import {MatExpansionModule} from "@angular/material/expansion";
import {NgIf} from "@angular/common";
import {ConfigurationClient} from "integration/client/configuration.client";
import {Platform} from "integration/platform";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatListModule} from "@angular/material/list";

@Component({
  selector: 'app-myfitnesspal',
  standalone: true,
  imports: [
    MfpCopyWellnessToCalendarComponent,
    MatExpansionModule,
    NgIf,
    MatProgressBarModule,
    MatTooltipModule,
    MatListModule,
  ],
  templateUrl: './myfitnesspal.component.html',
  styleUrl: './myfitnesspal.component.scss'
})
export class MyFitnessPalComponent implements OnInit {
  platformInfo: any = undefined;

  readonly menuPlatform = Platform.MYFITNESSPAL;

  constructor(
    private configurationClient: ConfigurationClient
  ) {
  }

  ngOnInit(): void {
    this.configurationClient.platformInfo(Platform.MYFITNESSPAL.key).subscribe(value => {
    this.platformInfo = value
  })

  }
}
