import {Component, Input, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {MatSelectModule} from "@angular/material/select";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatListModule} from "@angular/material/list";
import {Platform} from "integration/platform";
import {EventTypes} from "integration/event-types";
import {CopyEventsToCalendarComponent} from "app/components/copy-events-to-calendar/copy-events-to-calendar.component";

@Component({
  selector: 'tp-copy-events-to-calendar',
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
    CopyEventsToCalendarComponent,
  ],
  templateUrl: './tp-copy-events-to-calendar.component.html',
  styleUrl: './tp-copy-events-to-calendar.component.scss'
})
export class TpCopyEventsToCalendarComponent implements OnInit {
  @Input() currentPlatform: any = undefined;

  readonly Platform = Platform;
  readonly directions = [
    {title: "TrainingPeaks -> Intervals.icu", value: Platform.DIRECTION_TP_INT},
    {title: "Intervals.icu -> TrainingPeaks", value: Platform.DIRECTION_INT_TP},
  ]
  readonly eventTypes = EventTypes.eventTypes;
  // Default: all event types selected (only RACE for now)
  readonly selectedEventTypes = this.eventTypes.map(t => t.value);

  ngOnInit(): void {}
}
