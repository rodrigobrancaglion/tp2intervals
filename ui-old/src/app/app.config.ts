import {ApplicationConfig} from '@angular/core';
import {provideRouter, withHashLocation} from '@angular/router';
import {provideToastr} from 'ngx-toastr';
import {routes} from './app.routes';
import {provideAnimations} from '@angular/platform-browser/animations';
import {provideHttpClient, withInterceptors} from "@angular/common/http";
import {httpErrorInterceptor, httpHostInterceptor} from "../integration/http.interceptors";
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MAT_NATIVE_DATE_FORMATS} from "@angular/material/core";
import {CustomDateAdapter} from "./custom-date-adapter";

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withHashLocation()),
    provideAnimations(),
    provideToastr(),
    provideHttpClient(withInterceptors([httpErrorInterceptor, httpHostInterceptor])),
    // 1. Your Monday-first logic
    { provide: DateAdapter, useClass: CustomDateAdapter },

    // 2. The language/region
    { provide: MAT_DATE_LOCALE, useValue: 'en-GB' },

    // 3. The "stop-gap" for the MAT_DATE_FORMATS error
    { provide: MAT_DATE_FORMATS, useValue: MAT_NATIVE_DATE_FORMATS }
  ]
};
