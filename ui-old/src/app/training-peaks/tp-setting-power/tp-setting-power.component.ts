import {Component, OnInit} from '@angular/core';
import {SettingClient} from "integration/client/setting.client";
import {MatButtonModule} from "@angular/material/button";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {FormsModule} from "@angular/forms";
import {NotificationService} from "../../../integration/notification.service";
import {MatSnackBarModule} from "@angular/material/snack-bar";

@Component({
  selector: 'tp-setting-power',
  standalone: true,
  imports: [
    MatButtonModule,
    MatCheckboxModule,
    FormsModule,
    MatSnackBarModule
  ],
  templateUrl: './tp-setting-power.component.html',
  styleUrl: './tp-setting-power.component.scss'
})
export class TpSettingPowerComponent implements OnInit {
  isSyncing = false;
  schedulerEnabled = false;

  constructor(
    private settingClient: SettingClient,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.settingClient.getSchedulerStatus().subscribe({
      next: (status) => {
        this.schedulerEnabled = status.enabled;
      },
      error: () => {
        console.error("Failed to fetch scheduler status");
      }
    });
  }

  confirmSync() {
    this.isSyncing = true;
    this.settingClient.syncPowerZones().subscribe({
      next: (res) => {
        this.isSyncing = false;
        this.notificationService.success("Power zones synced successfully");
      },
      error: (err) => {
        this.isSyncing = false;
        this.notificationService.error("Failed to sync power zones");
      }
    });
  }

  onSchedulerToggle() {
    this.settingClient.toggleScheduler(this.schedulerEnabled).subscribe({
      next: (status) => {
        this.schedulerEnabled = status.enabled;
        const msg = this.schedulerEnabled ? "Scheduler enabled" : "Scheduler disabled";
        this.notificationService.success(msg);
      },
      error: () => {
        // Revert toggle on error
        this.schedulerEnabled = !this.schedulerEnabled;
        this.notificationService.error("Failed to update scheduler");
      }
    });
  }
}
