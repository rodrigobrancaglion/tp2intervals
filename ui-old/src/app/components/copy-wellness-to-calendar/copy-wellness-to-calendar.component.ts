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
import {WellnessTypes} from "integration/wellness-types";
import {WellnessClient} from "integration/client/wellness.client";

@Component({
  selector: 'copy-wellness-to-calendar',
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
  templateUrl: './copy-wellness-to-calendar.component.html',
  styleUrl: './copy-wellness-to-calendar.component.scss'
})
export class CopyWellnessToCalendarComponent implements OnInit {
  readonly Platform = Platform;
  readonly todayDate = new Date(new Date().getTime() - 24 * 60 * 60 * 1000)
  readonly tomorrowDate = new Date()

  @Input() currentPlatform: any = undefined;
  @Input() wellnessTypes: any[] = []
  @Input() selectedWellnessTypes: string[] = [];
  @Input() directions: any[] = []
  @Input() inProgress = false

  formGroup: FormGroup
  platformsInfo: any
  scheduleRequests: any[] = []

  constructor(
    private formBuilder: FormBuilder,
    private configurationClient: ConfigurationClient,
    private wellnessClient: WellnessClient,
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
    let startDate = formatDate(this.formGroup.controls['startDate'].value)
    let endDate = formatDate(this.formGroup.controls['endDate'].value)
    this.copyWellness(startDate, endDate);
  }

  today() {
    this.copyWellnessForOneDay(formatDate(this.todayDate));
  }

  scheduleToday() {
    const platformKey = this.currentPlatform?.key || this.currentPlatform;
    let startDate = null
    let endDate = null
    let direction = this.formGroup.value.direction
    let wellnessTypes = this.formGroup.value.wellnessTypes

    this.inProgress = true
    this.wellnessClient.scheduleCopyCalendarToCalendar(startDate, endDate, wellnessTypes, direction, platformKey).pipe(
      switchMap(() => this.loadScheduleRequests()),
      finalize(() => this.inProgress = false)
    ).subscribe(() => {
      this.notificationService.success(`Scheduled sync job`)
    })
  }

  mapWellnessTypesToTitles(values) {
    return values.map(value => WellnessTypes.getTitle(value))
  }

  private copyWellnessForOneDay(date) {
    this.copyWellness(date, date)
  }

  private copyWellness(startDate, endDate) {
    let direction = this.formGroup.value.direction
    let wellnessTypes = this.formGroup.value.wellnessTypes

    this.inProgress = true
    this.wellnessClient.copyCalendarToCalendar(startDate, endDate, wellnessTypes, direction).pipe(
      finalize(() => this.inProgress = false)
    ).subscribe((response) => {
      this.notificationService.success(
        `Wellness (Metrics): ${response.copied}<br> From ${response.startDate} to ${response.endDate}`)
    })
  }

  private getFormGroup() {
    return this.formBuilder.group({
      direction: [this.directions[0].value, Validators.required],
      wellnessTypes: [this.selectedWellnessTypes, Validators.required],
      startDate: [this.todayDate, Validators.required],
      endDate: [this.tomorrowDate, Validators.required],
    })
  }

  private loadScheduleRequests() {
    const platformKey = this.currentPlatform?.key || this.currentPlatform;

    return this.wellnessClient.getScheduleRequests(platformKey).pipe(
      tap(values => {
          this.scheduleRequests = values.map(value => {
            return {id: value.id, request: JSON.parse(value.requestJson)}
          })
          console.log(this.scheduleRequests)
        }
      )
    )
  }

  deleteJob(jobId: any) {
    this.inProgress = true
    this.wellnessClient.deleteScheduleRequest(jobId).pipe(
      switchMap(() => this.loadScheduleRequests()),
      finalize(() => this.inProgress = false)
    ).subscribe(() => {
      this.notificationService.success(`Deleted job`)
    })
  }
}
