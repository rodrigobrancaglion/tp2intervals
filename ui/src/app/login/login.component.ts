import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from './auth.service';
import {CommonModule} from '@angular/common';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ]
})
export class LoginComponent implements OnInit {
  authForm!: FormGroup;
  isRegisterMode = false;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    if (this.authService.getToken()) {
      this.router.navigate(['/home']);
    }

    this.authForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
      email: ['']
    });
  }

  toggleMode(): void {
    this.isRegisterMode = !this.isRegisterMode;
    this.authForm.reset();
  }

  onSubmit(): void {
    if (this.authForm.invalid) return;

    this.isLoading = true;
    const formValue = this.authForm.value;

    if (this.isRegisterMode) {
      this.authService.register(formValue).subscribe({
        next: () => {
          this.toastr.success('Registration successful. Please login.');
          this.toggleMode();
          this.isLoading = false;
        },
        error: (err) => {
          this.toastr.error(err.error?.message || 'Registration failed');
          this.isLoading = false;
        }
      });
    } else {
      this.authService.login(formValue).subscribe({
        next: () => {
          this.toastr.success('Login successful');
          this.router.navigate(['/home']);
          this.isLoading = false;
        },
        error: (err) => {
          this.toastr.error('Login failed. Please check your credentials.');
          this.isLoading = false;
        }
      });
    }
  }
}
