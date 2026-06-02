import {Component, OnInit} from '@angular/core';
import {Router, RouterLink} from "@angular/router";
import {EnvironmentService} from "integration/environment.service";
import {MatButtonModule} from "@angular/material/button";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatBadgeModule} from "@angular/material/badge";
import {forkJoin} from "rxjs";
import {GitHubClient} from "integration/client/github.client";
import * as semver from "semver";
import {MatTooltipModule} from "@angular/material/tooltip";

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
    MatBadgeModule,
    MatTooltipModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule
  ],
  templateUrl: './top-bar.component.html',
  styleUrl: './top-bar.component.scss'
})
export class TopBarComponent implements OnInit {
  appVersion: string
  updateAvailableBadgeHidden = true;
  githubLink = 'https://github.com/freekode/tp2intervals'

  menuButtons = [
    {name: 'Home', url: '/home'},
    {name: 'TrainingPeaks', url: '/training-peaks'},
    {name: 'Rouvy', url: '/rouvy'},
    {name: 'TrainerRoad', url: '/trainer-road'},
    {name: 'MyFitnessPal', url: '/myfitnesspal'},
    {name: 'Configuration', url: '/config'},
  ]

  constructor(
    protected router: Router,
    private githubClient: GitHubClient,
    private environmentService: EnvironmentService
  ) {
  }

  ngOnInit(): void {
    forkJoin([
      this.githubClient.getLatestRelease(),
      this.environmentService.getVersion(),
    ]).subscribe(result => {
      this.appVersion = result[1]
      let latestRelease = result[0]
      if (semver.gt(latestRelease.version, this.appVersion)) {
        this.updateAvailableBadgeHidden = false;
        this.githubLink = latestRelease.url
      }
      console.log(result);
    })
  }
}
