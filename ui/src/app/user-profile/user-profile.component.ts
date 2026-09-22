import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {AuthService} from '../login/auth.service';
import {NotificationService} from 'integration/notification.service';
import {AppTheme, ThemeService} from '../theme.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.scss'
})
export class UserProfileComponent implements OnInit {
  username: string = '';
  currentTheme: AppTheme = 'dark';
  isDeleting: boolean = false;
  showConfirmDialog: boolean = false;

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private themeService: ThemeService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.username = this.authService.getUsername() || 'User';
    this.currentTheme = this.themeService.currentTheme;
  }

  setTheme(theme: AppTheme): void {
    this.currentTheme = theme;
    this.themeService.setTheme(theme);
    this.notificationService.success(`Theme set to ${theme === 'dark' ? 'Dark' : 'Light'}`);
  }

  openConfirmDialog(): void {
    this.showConfirmDialog = true;
  }

  cancelDelete(): void {
    this.showConfirmDialog = false;
  }

  confirmDelete(): void {
    this.isDeleting = true;
    this.authService.deleteAccount().subscribe({
      next: () => {
        this.notificationService.success('Account deleted successfully');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.isDeleting = false;
        this.showConfirmDialog = false;
        this.notificationService.error(err?.error?.message || 'Failed to delete account');
      }
    });
  }
}
