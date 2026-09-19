import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {MatSelectModule} from "@angular/material/select";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {Platform} from "integration/platform";
import {formatDate} from "utils/date-formatter";
import {MatDividerModule} from "@angular/material/divider";
import {MatListModule} from "@angular/material/list";
import {NgIf} from "@angular/common";
import {ConfigurationClient} from "integration/client/configuration.client";
import {finalize, switchMap, tap} from "rxjs";
import {NotificationService} from "integration/notification.service";
import {MatTooltipModule} from "@angular/material/tooltip";
import {EventsClient} from "integration/client/events.client";
import {EventTypes} from "integration/event-types";

@Component({
  selector: 'copy-events-to-calendar',
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
    MatDividerModule,
    MatListModule,
    NgIf,
    MatTooltipModule,
  ],
  templateUrl: './copy-events-to-calendar.component.html',
  styleUrl: './copy-events-to-calendar.component.scss'
})
export class CopyEventsToCalendarComponent implements OnInit {
  readonly Platform = Platform;
  readonly todayDate = new Date()
  readonly tomorrowDate = new Date(new Date().getTime() + 24 * 60 * 60 * 1000)

  @Input() currentPlatform: any = undefined;
  @Input() eventTypes: any[] = []
  @Input() selectedEventTypes: string[] = [];
  @Input() directions: any[] = []
  @Input() inProgress = false

  formGroup: FormGroup
  platformsInfo: any
  scheduleRequests: any[] = []

  constructor(
    private formBuilder: FormBuilder,
    private configurationClient: ConfigurationClient,
    private eventsClient: EventsClient,
    private notificationService: NotificationService
  ) {
  }

  ngOnInit(): void {
    this.configurationClient.getAllPlatformInfo().subscribe(value => {
      this.platformsInfo = value
    })
    this.formGroup = this.getFormGroup();
    this.loadScheduleRequests().subscribe()
  }

  submit() {
    const startDate = formatDate(this.formGroup.controls['startDate'].value)
    const endDate = formatDate(this.formGroup.controls['endDate'].value)
    this.copyEvents(startDate, endDate);
  }

  today() {
    this.copyEvents(formatDate(this.todayDate), formatDate(this.todayDate));
  }

  scheduleToday() {
    const direction = this.formGroup.value.direction
    const types = this.formGroup.value.eventTypes

    this.inProgress = true
    this.eventsClient.scheduleCopyCalendarToCalendar(null, null, types, direction).pipe(
      switchMap(() => this.loadScheduleRequests()),
      finalize(() => this.inProgress = false)
    ).subscribe(() => {
      this.notificationService.success(`Scheduled event sync job`)
    })
  }

  mapEventTypesToTitles(values) {
    return values.map(value => EventTypes.getTitle(value)).join(', ')
  }

  private copyEvents(startDate, endDate) {
    const direction = this.formGroup.value.direction
    const types = this.formGroup.value.eventTypes

    this.inProgress = true
    this.eventsClient.copyCalendarToCalendar(startDate, endDate, types, direction).pipe(
      finalize(() => this.inProgress = false)
    ).subscribe((response) => {
      this.notificationService.success(
        `Copied: ${response.copied}<br> From ${response.startDate} to ${response.endDate}`)
    })
  }

  private getFormGroup() {
    return this.formBuilder.group({
      direction: [this.directions[0].value, Validators.required],
      eventTypes: [this.selectedEventTypes, Validators.required],
      startDate: [this.todayDate, Validators.required],
      endDate: [this.tomorrowDate, Validators.required],
    })
  }

  private loadScheduleRequests() {
    const platform = this.formGroup.value.direction?.sourcePlatform
    return this.eventsClient.getScheduleRequests(platform).pipe(
      tap(values => {
        this.scheduleRequests = values.map(value => ({
          id: value.id,
          request: JSON.parse(value.requestJson)
        }))
      })
    )
  }

  deleteJob(jobId: any) {
    this.inProgress = true
    this.eventsClient.deleteScheduleRequest(jobId).pipe(
      switchMap(() => this.loadScheduleRequests()),
      finalize(() => this.inProgress = false)
    ).subscribe(() => {
      this.notificationService.success(`Deleted job`)
    })
  }
}
