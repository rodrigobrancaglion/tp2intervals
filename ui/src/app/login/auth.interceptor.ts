import {HttpInterceptorFn} from '@angular/common/http';
import {inject} from '@angular/core';
import {AuthService} from './auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  const externalUrls = ['api.github.com'];
  const isExternalUrl = externalUrls.some(url => req.url.includes(url));

  if (isExternalUrl || !token) {
    return next(req);
  }

  if (token) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}` //TODO
      }
    });
    return next(authReq);
  }

  return next(req);
};
