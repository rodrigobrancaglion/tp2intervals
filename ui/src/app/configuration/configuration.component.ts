import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ConfigData} from 'integration/config-data';
import {Router} from '@angular/router';
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {finalize} from "rxjs";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {NgIf} from "@angular/common";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {NotificationService} from "integration/notification.service";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {ConfigurationClient} from "integration/client/configuration.client";
import {MatOptionModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatTooltipModule} from "@angular/material/tooltip";

import {environment} from 'environments/environment';

@Component({
  selector: 'app-configuration',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressBarModule,
    MatSnackBarModule,
    NgIf,
    MatCheckboxModule,
    MatOptionModule,
    MatSelectModule,
    MatExpansionModule,
    MatTooltipModule
  ],
  templateUrl: './configuration.component.html',
  styleUrl: './configuration.component.scss'
})
export class ConfigurationComponent implements OnInit {
  public config = environment;

  formGroup: FormGroup = this.formBuilder.group({
    'intervals.api-key': [this.config.intervals_api_key, Validators.required],
    'intervals.athlete-id': [null, Validators.required],
    'intervals.power-range': [null, [Validators.required, Validators.min(0), Validators.max(100)]],
    'intervals.hr-range': [null, [Validators.required, Validators.min(0), Validators.max(100)]],
    'intervals.pace-range': [null, [Validators.required, Validators.min(0), Validators.max(100)]],
    'training-peaks.auth-cookie': [null, [Validators.pattern('^Production_tpAuth=[a-zA-Z0-9-_]*$')]],
    'trainer-road.auth-cookie': [null, [Validators.pattern('^SharedTrainerRoadAuth=.*$')]],
    'trainer-road.remove-html-tags': [null, Validators.required],
    'mfp.session-cookie': [null],
    'mfp.session-token-cookie': [null],
    'mfp.remember-me-cookie': [null],
    'mfp.user-id': [null, Validators.required],
    'mfp.username': [null, Validators.required],
    'strava.client-id': [null],
    'strava.client-secret': [null],
    'strava.refresh-token': [null],
    'rouvy.email': [null],
    'rouvy.password': [null],
    'general.debug-mode': [null, Validators.required],
  });

  inProgress = false;

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private configClient: ConfigurationClient,
    private notificationService: NotificationService
  ) {
  }

  ngOnInit(): void {
    this.inProgress = true
    this.configClient.getConfig().subscribe(config => {
      this.formGroup.patchValue(config.config);

      if (!this.formGroup.get('intervals.api-key')?.value) {
        this.formGroup.patchValue({
          'intervals.api-key': this.config.intervals_api_key
        });
      }
      if (!this.formGroup.get('intervals.athlete-id')?.value) {
        this.formGroup.patchValue({
          'intervals.athlete-id': this.config.intervals_athlete_id
        });
      }
      if (!this.formGroup.get('training-peaks.auth-cookie')?.value) {
        this.formGroup.patchValue({
          'training-peaks.auth-cookie': this.config.trainingpeaks_auth_cookie
        });
      }
      if (!this.formGroup.get('mfp.session-cookie')?.value && this.config.mfp_session_cookie) {
        this.formGroup.patchValue({ 'mfp.session-cookie': this.config.mfp_session_cookie });
      }
      if (!this.formGroup.get('mfp.session-token-cookie')?.value && this.config.mfp_session_token_cookie) {
        this.formGroup.patchValue({ 'mfp.session-token-cookie': this.config.mfp_session_token_cookie });
      }
      if (!this.formGroup.get('mfp.remember-me-cookie')?.value && this.config.mfp_remember_me_cookie) {
        this.formGroup.patchValue({ 'mfp.remember-me-cookie': this.config.mfp_remember_me_cookie });
      }
      if (!this.formGroup.get('mfp.user-id')?.value && this.config.mfp_user_id) {
        this.formGroup.patchValue({ 'mfp.user-id': this.config.mfp_user_id });
      }
      if (!this.formGroup.get('mfp.username')?.value && this.config.mfp_username) {
        this.formGroup.patchValue({ 'mfp.username': this.config.mfp_username });
      }
      if (!this.formGroup.get('strava.client-id')?.value && this.config.strava_client_id) {
        this.formGroup.patchValue({ 'strava.client-id': this.config.strava_client_id });
      }
      if (!this.formGroup.get('strava.client-secret')?.value && this.config.strava_client_secret) {
        this.formGroup.patchValue({ 'strava.client-secret': this.config.strava_client_secret });
      }
      if (!this.formGroup.get('strava.refresh-token')?.value && this.config.strava_refresh_token) {
        this.formGroup.patchValue({ 'strava.refresh-token': this.config.strava_refresh_token });
      }
      if (!this.formGroup.get('rouvy.email')?.value && this.config.rouvy_email) {
        this.formGroup.patchValue({ 'rouvy.email': this.config.rouvy_email });
      }
      if (!this.formGroup.get('rouvy.password')?.value && this.config.rouvy_password) {
        this.formGroup.patchValue({ 'rouvy.password': this.config.rouvy_password });
      }

      this.inProgress = false
      this.listenTrainingPeaksCookie()
      this.listenTrainerRoadCookie()
    });
  }

  onSubmit(): void {
    this.inProgress = true
    let newConfiguration = new ConfigData(this.formGroup.getRawValue());

    console.log(newConfiguration)
    this.configClient.updateConfig(newConfiguration).pipe(
      finalize(() => this.inProgress = false)
    ).subscribe(() => {
      // Sync all form fields back to environment so the session reflects the saved values
      const v = this.formGroup.getRawValue();
      this.config.intervals_api_key = v['intervals.api-key'] ?? this.config.intervals_api_key;
      this.config.intervals_athlete_id = v['intervals.athlete-id'] ?? this.config.intervals_athlete_id;
      this.config.trainingpeaks_auth_cookie = v['training-peaks.auth-cookie'] ?? this.config.trainingpeaks_auth_cookie;
      this.config.mfp_session_cookie = v['mfp.session-cookie'] ?? this.config.mfp_session_cookie;
      this.config.mfp_session_token_cookie = v['mfp.session-token-cookie'] ?? this.config.mfp_session_token_cookie;
      this.config.mfp_remember_me_cookie = v['mfp.remember-me-cookie'] ?? this.config.mfp_remember_me_cookie;
      this.config.mfp_user_id = v['mfp.user-id'] ?? this.config.mfp_user_id;
      this.config.mfp_username = v['mfp.username'] ?? this.config.mfp_username;
      this.config.strava_client_id = v['strava.client-id'] ?? this.config.strava_client_id;
      this.config.strava_client_secret = v['strava.client-secret'] ?? this.config.strava_client_secret;
      this.config.strava_refresh_token = v['strava.refresh-token'] ?? this.config.strava_refresh_token;
      this.config.rouvy_email = v['rouvy.email'] ?? this.config.rouvy_email;
      this.config.rouvy_password = v['rouvy.password'] ?? this.config.rouvy_password;

      this.notificationService.success('Configuration successfully saved')
      this.router.navigate(['/home']);
    });
  }

  listenTrainerRoadCookie() {
    this.formGroup.controls['trainer-road.auth-cookie'].valueChanges.subscribe(value => {
      let split = value.split('SharedTrainerRoadAuth=');
      if (split.length != 2) {
        return
      }
      let splitValue = split[1].split(';')[0]
      let newValue = `SharedTrainerRoadAuth=${splitValue}`
      if (value === newValue) {
        return;
      }
      this.formGroup.controls['trainer-road.auth-cookie'].setValue(newValue)
    })
  }

  listenTrainingPeaksCookie() {
    this.formGroup.controls['training-peaks.auth-cookie'].valueChanges.subscribe(value => {
      let split = value.split('Production_tpAuth=');
      if (split.length != 2) {
        return
      }
      let splitValue = split[1].split(';')[0]
      let newValue = `Production_tpAuth=${splitValue}`
      if (value === newValue) {
        return;
      }
      this.formGroup.controls['training-peaks.auth-cookie'].setValue(newValue)
    })
  }
}
