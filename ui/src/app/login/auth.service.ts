import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable, tap} from 'rxjs';
import {Router} from '@angular/router';
import {ThemeService} from '../theme.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_ID_KEY = 'id_user';
  private readonly USERNAME_KEY = 'username';

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router,
    private themeService: ThemeService
  ) { }

  login(credentials: any): Observable<any> {
    return this.http.post('/api/auth/login', credentials).pipe(
      tap((response: any) => {
        if (response && response.token) {
          this.setSession(response);
        }
      })
    );
  }

  register(userData: any): Observable<any> {
    return this.http.post('/api/auth/register', userData);
  }

  logout() {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_ID_KEY);
    localStorage.removeItem(this.USERNAME_KEY);
    this.isAuthenticatedSubject.next(false);
    this.themeService.loadThemeForCurrentUser();
    this.router.navigate(['/login']);
  }

  private setSession(authResult: any) {
    localStorage.setItem(this.TOKEN_KEY, authResult.token);
    localStorage.setItem(this.USER_ID_KEY, authResult.id.toString());
    localStorage.setItem(this.USERNAME_KEY, authResult.username);
    this.isAuthenticatedSubject.next(true);
    this.themeService.loadThemeForCurrentUser();
  }

  deleteAccount(): Observable<any> {
    return this.http.delete('/api/auth/user').pipe(
      tap(() => {
        this.logout();
      })
    );
  }

  public getUsername(): string | null {
    return localStorage.getItem(this.USERNAME_KEY);
  }

  public getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  public getUserId(): string | null {
    return localStorage.getItem(this.USER_ID_KEY);
  }

  private hasToken(): boolean {
    return !!this.getToken();
  }
}
