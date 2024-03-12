import { Component, OnInit } from '@angular/core';
import { MatGridListModule } from "@angular/material/grid-list";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { AsyncPipe, NgIf } from "@angular/common";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { MatSelectModule } from "@angular/material/select";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { WorkoutClient } from "infrastructure/workout.client";
import { ConfigurationClient } from "infrastructure/configuration.client";
import { NotificationService } from "infrastructure/notification.service";
import { debounceTime, filter, finalize, map, Observable, switchMap, tap } from "rxjs";
import { LibraryClient } from "infrastructure/library-client.service";
import { Platform } from "infrastructure/platform";
import { MatAutocompleteModule } from "@angular/material/autocomplete";

@Component({
  selector: 'tr-copy-workout',
  standalone: true,
  imports: [
    MatGridListModule,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
    MatProgressBarModule,
    NgIf,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSnackBarModule,
    MatSelectModule,
    MatCheckboxModule,
    AsyncPipe,
    MatAutocompleteModule
  ],
  templateUrl: './tr-copy-workout.component.html',
  styleUrl: './tr-copy-workout.component.scss'
})
export class TrCopyWorkoutComponent implements OnInit {

  formGroup: FormGroup = this.formBuilder.group({
    trWorkoutDetails: [null, [Validators.required, Validators.minLength(3)]],
    intervalsPlan: [null, Validators.required],
  });

  searchInProgress = false
  submitInProgress = false

  workouts: Observable<any[]>;
  intervalsLibraryItem: Observable<{ name: any; value: any }[]>;

  constructor(
    private formBuilder: FormBuilder,
    private workoutClient: WorkoutClient,
    private planClient: LibraryClient,
    private configurationClient: ConfigurationClient,
    private notificationService: NotificationService
  ) {
  }

  ngOnInit(): void {
    this.loadPlans();
    this.subscribeOnWorkoutNameChange();
  }

  copyWorkoutSubmit() {
    this.submitInProgress = true
    let plan = this.formGroup.value.plan
    let direction = {sourcePlatform: 'TRAINING_PEAKS', targetPlatform: 'INTERVALS'}
    // this.planClient.copyLibrary(plan, 'sadf', direction).pipe(
    //   finalize(() => this.submitInProgress = false)
    // ).subscribe((response) => {
    //   this.notificationService.success(
    //     `Plan name: ${response.planName}\nCopied workouts: ${response.workouts}`)
    // })
  }

  displayFn(workout): string {
    return workout ? workout.name : '';
  }

  private loadPlans() {
    this.formGroup.disable()
    this.intervalsLibraryItem = this.planClient.getLibraries(Platform.INTERVALS.key).pipe(
      map(plans => plans.map(plan => {
          return {name: plan.name, value: plan}
        })
      ),
      finalize(() => {
        this.formGroup.enable()
      })
    )
  }

  private subscribeOnWorkoutNameChange() {
    this.workouts = this.formGroup.controls['trWorkoutDetails'].valueChanges.pipe(
      debounceTime(500),
      filter(() => this.formGroup.controls['trWorkoutDetails'].valid),
      tap(() => {
        this.searchInProgress = true
      }),
      switchMap(value => this.workoutClient.findWorkoutsByName(Platform.TRAINER_ROAD.key, value).pipe(
        finalize(() => {
          this.searchInProgress = false
        })
      ))
    )
  }
}
