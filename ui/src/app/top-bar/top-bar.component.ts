import {Component, OnInit} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from "@angular/router";
import {EnvironmentService} from "integration/environment.service";
import {MatButtonModule} from "@angular/material/button";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatBadgeModule} from "@angular/material/badge";
import {forkJoin, Observable} from "rxjs";
import {GitHubClient} from "integration/client/github.client";
import * as semver from "semver";
import {MatTooltipModule} from "@angular/material/tooltip";
import {AsyncPipe, NgClass, NgIf} from "@angular/common";
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {ConnectionStatusService} from "app/connection-status.service";
import {AuthService} from "app/login/auth.service";

interface MenuItem {
  name: string;
  url?: string;
  icon: string;
  id: string;
  children?: { name: string; url: string; icon: string }[];
}

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
    MatMenuModule,
    NgIf,
    NgClass,
    AsyncPipe
  ],
  templateUrl: './top-bar.component.html',
  styleUrl: './top-bar.component.scss'
})
export class TopBarComponent implements OnInit {
  appVersion: string
  updateAvailableBadgeHidden = true;
  githubLink = 'https://github.com/freekode/tp2intervals'
  connectedPlatforms$: Observable<{ [key: string]: boolean }>;
  isAuthenticated$: Observable<boolean>;

  menuButtons: MenuItem[] = [
    {name: 'Home', url: '/home', icon: 'bi bi-house-door', id: 'home'},
    {name: 'TrainingPeaks', url: '/training-peaks', icon: 'assets/platforms/tp.jpg', id: 'training-peaks'},
    {name: 'Rouvy', url: '/rouvy', icon: 'assets/platforms/rouvy.jpg', id: 'rouvy'},
    {name: 'TrainerRoad', url: '/trainer-road', icon: 'assets/platforms/tr.jpg', id: 'trainer-road'},
    {name: 'MyFitnessPal', url: '/myfitnesspal', icon: 'assets/platforms/mfp.jpg', id: 'myfitnesspal'},
    {
      name: 'Configuration',
      icon: 'bi bi-sliders',
      id: 'config',
      children: [
        {name: 'System Configuration', url: '/config', icon: 'bi bi-gear'},
        {name: 'User', url: '/user', icon: 'bi bi-person'}
      ]
    },
  ]

  configExpanded: boolean = true;
  isSidebarExpanded: boolean = true;

  toggleSidebar(): void {
    this.isSidebarExpanded = !this.isSidebarExpanded;
    if (!this.isSidebarExpanded) {
      this.configExpanded = false;
    }
    localStorage.setItem('sidebar_expanded', this.isSidebarExpanded.toString());
  }

  toggleConfigSubmenu(): void {
    if (!this.isSidebarExpanded) {
      this.isSidebarExpanded = true;
      this.configExpanded = true;
      localStorage.setItem('sidebar_expanded', 'true');
    } else {
      this.configExpanded = !this.configExpanded;
    }
  }

  isConfigRouteActive(): boolean {
    return this.router.url === '/config' || this.router.url === '/user';
  }

  constructor(
    protected router: Router,
    private githubClient: GitHubClient,
    private environmentService: EnvironmentService,
    private connectionStatusService: ConnectionStatusService,
    private authService: AuthService
  ) {
    const saved = localStorage.getItem('sidebar_expanded');
    if (saved !== null) {
      this.isSidebarExpanded = saved === 'true';
    }
  }

  ngOnInit(): void {
    this.connectedPlatforms$ = this.connectionStatusService.connectedPlatforms$;
    this.isAuthenticated$ = this.authService.isAuthenticated$;

    forkJoin([
      this.githubClient.getLatestRelease(),
      this.environmentService.getVersion()
    ]).subscribe(result => {
      this.appVersion = result[1]
      let latestRelease = result[0]

      if (semver.gt(latestRelease.version, this.appVersion)) {
        this.updateAvailableBadgeHidden = false;
        this.githubLink = latestRelease.url
      }
    })
  }

  logout() {
    this.authService.logout();
  }
}

