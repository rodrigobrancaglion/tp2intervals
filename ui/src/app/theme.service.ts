import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';

export type AppTheme = 'dark' | 'light';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly BASE_THEME_KEY = 'app_theme';
  private currentThemeSubject: BehaviorSubject<AppTheme>;
  public currentTheme$: Observable<AppTheme>;

  constructor() {
    const initialTheme = this.loadThemeForCurrentUser();
    this.currentThemeSubject = new BehaviorSubject<AppTheme>(initialTheme);
    this.currentTheme$ = this.currentThemeSubject.asObservable();
    this.applyTheme(initialTheme);
  }

  private getStorageKey(): string {
    const username = localStorage.getItem('username');
    return username ? `${this.BASE_THEME_KEY}_${username}` : this.BASE_THEME_KEY;
  }

  public loadThemeForCurrentUser(): AppTheme {
    const key = this.getStorageKey();
    const savedTheme = (localStorage.getItem(key) as AppTheme) || 'dark';
    this.applyTheme(savedTheme);
    if (this.currentThemeSubject && this.currentThemeSubject.value !== savedTheme) {
      this.currentThemeSubject.next(savedTheme);
    }
    return savedTheme;
  }

  public get currentTheme(): AppTheme {
    return this.currentThemeSubject.value;
  }

  public setTheme(theme: AppTheme): void {
    const key = this.getStorageKey();
    localStorage.setItem(key, theme);
    this.currentThemeSubject.next(theme);
    this.applyTheme(theme);
  }

  private applyTheme(theme: AppTheme): void {
    const root = document.documentElement;
    if (theme === 'light') {
      root.classList.add('light-theme');
      root.classList.remove('dark-theme');
    } else {
      root.classList.add('dark-theme');
      root.classList.remove('light-theme');
    }
  }
}
