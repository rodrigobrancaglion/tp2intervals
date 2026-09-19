import {Router} from "@angular/router";
import {inject} from "@angular/core";
import {AuthService} from "../login/auth.service";

export function canActivateHome() {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.getToken()) {
    return true;
  }
  
  router.navigate(['/login']);
  return false;
}
