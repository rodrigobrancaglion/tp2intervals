import {Component, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {TopBarComponent} from "app/top-bar/top-bar.component";
import {AuthService} from "app/login/auth.service";
import {AsyncPipe, NgIf} from "@angular/common";
import {Observable} from "rxjs";


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    TopBarComponent,
    NgIf,
    AsyncPipe
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  isAuthenticated$: Observable<boolean>;

  constructor(
    private authService: AuthService
  ) {
  }

  ngOnInit(): void {
    this.isAuthenticated$ = this.authService.isAuthenticated$;
  }
}
