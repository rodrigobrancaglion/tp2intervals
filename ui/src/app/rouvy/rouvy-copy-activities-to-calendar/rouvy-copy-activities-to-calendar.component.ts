import {Component, Input, OnInit} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {MatSelectModule} from "@angular/material/select";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatDividerModule} from "@angular/material/divider";
import {MatListModule} from "@angular/material/list";
import {MatTooltipModule} from "@angular/material/tooltip";
import {NgIf} from "@angular/common";
import {Platform} from "integration/platform";
import {ActivitiesTypes} from "integration/activities-types";
import {ActivityClient} from "integration/client/activity.client";
import {NotificationService} from "integration/notification.service";
import {formatDate} from "utils/date-formatter";
import {finalize, switchMap, tap} from "rxjs";

@Component({
  selector: 'rouvy-copy-activities-to-calendar',
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
    MatTooltipModule,
    NgIf,
  ],
  templateUrl: './rouvy-copy-activities-to-calendar.component.html',
  styleUrl: './rouvy-copy-activities-to-calendar.component.scss'
})
export class RouvyCopyActivitiesToCalendarComponent implements OnInit {
  @Input() currentPlatform: any = undefined;

  readonly Platform = Platform;
  readonly todayDate = new Date();
  readonly tomorrowDate = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);

  readonly destinations = [
    { key: Platform.TRAINING_PEAKS.key, title: Platform.TRAINING_PEAKS.title },
    { key: Platform.STRAVA.key, title: Platform.STRAVA.title },
    { key: Platform.INTERVALS.key, title: Platform.INTERVALS.title },
  ];

  readonly activitiesTypes = [
    {title: "Activity", value: "ACTIVITY"},
  ];
  readonly selectedActivitiesTypes = this.activitiesTypes.map(t => t.value);

  formGroup: FormGroup;
  inProgress = false;
  isScheduling = false;
  scheduleRequests: any[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private activityClient: ActivityClient,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.formGroup = this.formBuilder.group({
      destinations: [
        [Platform.TRAINING_PEAKS.key, Platform.STRAVA.key, Platform.INTERVALS.key],
        [this.atLeastOneDestinationValidator]
      ],
      activitiesTypes: [this.selectedActivitiesTypes, Validators.required],
      startDate: [this.todayDate, Validators.required],
      endDate: [this.tomorrowDate, Validators.required],
    });

    this.loadScheduleRequests().subscribe();
  }

  private atLeastOneDestinationValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value || !Array.isArray(value) || value.length === 0) {
      return { required: true };
    }
    return null;
  }

  isDestinationSelected(key: string): boolean {
    const list = this.formGroup?.controls['destinations']?.value || [];
    return list.includes(key);
  }

  toggleDestination(key: string, checked: boolean) {
    const current = [...(this.formGroup.controls['destinations'].value || [])];
    if (checked) {
      if (!current.includes(key)) {
        current.push(key);
      }
    } else {
      const idx = current.indexOf(key);
      if (idx !== -1) {
        current.splice(idx, 1);
      }
    }
    this.formGroup.controls['destinations'].setValue(current);
    this.formGroup.controls['destinations'].markAsTouched();
    this.formGroup.controls['destinations'].updateValueAndValidity();
  }

  submit() {
    const startDate = formatDate(this.formGroup.controls['startDate'].value);
    const endDate = formatDate(this.formGroup.controls['endDate'].value);
    this.copyActivities(startDate, endDate);
  }

  today() {
    const todayFormatted = formatDate(this.todayDate);
    this.copyActivities(todayFormatted, todayFormatted);
  }

  scheduleToday() {
    const platformKey = this.currentPlatform?.key || this.currentPlatform || Platform.ROUVY.key;
    const selectedDestinations = this.formGroup.value.destinations;
    const activitiesTypes = this.formGroup.value.activitiesTypes;

    this.isScheduling = true;
    this.activityClient.scheduleCopyCalendarToCalendar(
      null,
      null,
      activitiesTypes,
      {
        sourcePlatform: Platform.ROUVY.key,
        targetPlatforms: selectedDestinations
      },
      platformKey
    ).pipe(
      switchMap(() => this.loadScheduleRequests()),
      finalize(() => this.isScheduling = false)
    ).subscribe({
      next: () => {
        this.notificationService.success('Scheduled sync job');
      },
      error: () => {}
    });
  }

  deleteJob(jobId: any) {
    this.isScheduling = true;
    this.activityClient.deleteScheduleRequest(jobId).pipe(
      switchMap(() => this.loadScheduleRequests()),
      finalize(() => this.isScheduling = false)
    ).subscribe({
      next: () => {
        this.notificationService.success('Deleted job');
      },
      error: () => {}
    });
  }

  mapActivitiesTypesToTitles(values: any[]): string {
    return (values || []).map(value => ActivitiesTypes.getTitle(value)).join(', ');
  }

  getTargetsTitle(req: any): string {
    if (req.targetPlatforms && req.targetPlatforms.length > 0) {
      return req.targetPlatforms.map(p => Platform.getTitle(p) || p).join(', ');
    }
    if (req.targetPlatform) {
      return Platform.getTitle(req.targetPlatform) || req.targetPlatform;
    }
    return '';
  }

  private copyActivities(startDate: string, endDate: string) {
    const selectedDestinations = this.formGroup.value.destinations;
    const activitiesTypes = this.formGroup.value.activitiesTypes;

    this.inProgress = true;
    this.activityClient.copyCalendarToCalendar(startDate, endDate, activitiesTypes, {
      sourcePlatform: Platform.ROUVY.key,
      targetPlatforms: selectedDestinations
    }).pipe(
      finalize(() => this.inProgress = false)
    ).subscribe({
      next: (response) => {
        const destTitles = selectedDestinations.map(d => Platform.getTitle(d) || d).join(', ');
        this.notificationService.success(
          `Planned: ${response.filteredOut}<br> Saved: ${response.copied}<br> Destinations: ${destTitles}<br> From ${response.startDate} to ${response.endDate}`
        );
      },
      error: () => {}
    });
  }

  private loadScheduleRequests() {
    const platformKey = this.currentPlatform?.key || this.currentPlatform || Platform.ROUVY.key;

    return this.activityClient.getScheduleRequests(platformKey).pipe(
      tap(values => {
        this.scheduleRequests = (values || []).map(value => {
          return { id: value.id, request: JSON.parse(value.requestJson) };
        });
      })
    );
  }
}
