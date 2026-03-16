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
import {TrainingTypes} from "integration/training-types";
import {MatListModule} from "@angular/material/list";
import {
  CopyCalendarToCalendarComponent
} from "app/components/copy-calendar-to-calendar/copy-calendar-to-calendar.component";

@Component({
  selector: 'tp-copy-calendar-to-calendar',
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
    CopyCalendarToCalendarComponent,
  ],
  templateUrl: './tp-copy-calendar-to-calendar.component.html',
  styleUrl: './tp-copy-calendar-to-calendar.component.scss'
})
export class TpCopyCalendarToCalendarComponent implements OnInit {
  readonly Platform = Platform;
  readonly directions = [
    { title: "TrainingPeaks -> Intervals.icu", value: Platform.DIRECTION_TP_INT },
    { title: "Intervals.icu -> TrainingPeaks", value: Platform.DIRECTION_INT_TP },
  ]
  readonly trainingTypes = TrainingTypes.trainingTypes;
  readonly selectedTrainingTypes = ['BIKE', 'VIRTUAL_BIKE', "MTB", 'DAY_OFF', 'BRICK'];

  constructor() {
  }

  ngOnInit(): void {
  }
}
