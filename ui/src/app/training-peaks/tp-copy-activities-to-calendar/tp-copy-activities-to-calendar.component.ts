import {Component, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {MatSelectModule} from "@angular/material/select";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {Platform} from "integration/platform";
import {ActivitiesTypes} from "integration/activities-types";
import {MatListModule} from "@angular/material/list";
import {
  CopyActivitiesToCalendarComponent
} from "app/components/copy-activities-to-calendar/copy-activities-to-calendar.component";

@Component({
  selector: 'tp-copy-activities-to-calendar',
  standalone: true,
  imports: [
    FormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
    MatProgressBarModule,
    MatDatepickerModule,
    MatSnackBarModule,
    MatSelectModule,
    MatCheckboxModule,
    MatListModule,
    CopyActivitiesToCalendarComponent,
  ],
  templateUrl: './tp-copy-activities-to-calendar.component.html',
  styleUrl: './tp-copy-activities-to-calendar.component.scss'
})
export class TpCopyActivitiesToCalendarComponent implements OnInit {
  readonly Platform = Platform;
  readonly directions = [
    {title: "TrainingPeaks -> Intervals.icu", value: Platform.DIRECTION_TP_INT},
    {title: "TrainingPeaks -> Strava",        value: Platform.DIRECTION_TP_STRAVA},
    {title: "Intervals.icu -> TrainingPeaks", value: Platform.DIRECTION_INT_TP},
  ]
  readonly activitiesTypes = ActivitiesTypes.activitiesTypes;
  readonly selectedActivitiesTypes = ['ACTIVITY', 'RPE', 'FEEL'];

  constructor() {
  }

  ngOnInit(): void {
  }
}
