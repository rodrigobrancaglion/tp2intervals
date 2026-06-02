import {Component, OnInit} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from "@angular/router";
import {EnvironmentService} from "integration/environment.service";
import {MatButtonModule} from "@angular/material/button";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatBadgeModule} from "@angular/material/badge";
import {forkJoin} from "rxjs";
import {GitHubClient} from "integration/client/github.client";
import {ConfigurationClient} from "integration/client/configuration.client";
import * as semver from "semver";
import {MatTooltipModule} from "@angular/material/tooltip";
import {NgClass, NgIf} from "@angular/common";

import {MatSidenavModule} from '@angular/material/sidenav';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-top-bar',
  standalone: true,
  imports: [
    MatButtonModule,
    MatToolbarModule,
    RouterLink,
    RouterLinkActive,
    MatBadgeModule,
    MatTooltipModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    NgIf,
    NgClass
  ],
  templateUrl: './top-bar.component.html',
  styleUrl: './top-bar.component.scss'
})
export class TopBarComponent implements OnInit {
  appVersion: string
  updateAvailableBadgeHidden = true;
  githubLink = 'https://github.com/freekode/tp2intervals'

  // Dict to store connection status
  connectedPlatforms: { [key: string]: boolean } = {
    'training-peaks': false,
    'wahoo': false,
    'rouvy': false,
    'myfitnesspal': false,
    'trainer-road': false,
    'intervals': false
  };

  menuButtons = [
    {name: 'Home', url: '/home', icon: 'bi bi-house-door', id: 'home'},
    {name: 'TrainingPeaks', url: '/training-peaks', icon: 'assets/platforms/tp.jpg', id: 'training-peaks'},
    {name: 'Rouvy', url: '/rouvy', icon: 'assets/platforms/rouvy.jpg', id: 'rouvy'},
    {name: 'TrainerRoad', url: '/trainer-road', icon: 'assets/platforms/tr.jpg', id: 'trainer-road'},
    {name: 'MyFitnessPal', url: '/myfitnesspal', icon: 'assets/platforms/mfp.jpg', id: 'myfitnesspal'},
    {name: 'Configuration', url: '/config', icon: 'bi bi-sliders', id: 'config'},
  ]

  constructor(
    protected router: Router,
    private githubClient: GitHubClient,
    private environmentService: EnvironmentService,
    private configClient: ConfigurationClient
  ) {
  }

  ngOnInit(): void {
    forkJoin([
      this.githubClient.getLatestRelease(),
      this.environmentService.getVersion(),
      this.configClient.getConfig()
    ]).subscribe(result => {
      this.appVersion = result[1]
      let latestRelease = result[0]
      let configData = result[2]?.config || {}

      if (semver.gt(latestRelease.version, this.appVersion)) {
        this.updateAvailableBadgeHidden = false;
        this.githubLink = latestRelease.url
      }

      // Compute connection status for each platform
      this.connectedPlatforms['intervals'] = !!configData['intervals.api-key'] && !!configData['intervals.athlete-id'];
      this.connectedPlatforms['training-peaks'] = !!configData['training-peaks.auth-cookie'];
      this.connectedPlatforms['trainer-road'] = !!configData['trainer-road.auth-cookie'];
      this.connectedPlatforms['myfitnesspal'] = !!configData['mfp.session-cookie'] || !!configData['mfp.user-id'];
      this.connectedPlatforms['rouvy'] = !!configData['rouvy.email'];
      this.connectedPlatforms['wahoo'] = !!configData['wahoo.refresh-token'];

      console.log('App version & connection status evaluated:', this.connectedPlatforms);
    })
  }
}
