import {Routes} from '@angular/router';
import {HomeComponent} from "app/home/home.component";
import {ConfigurationComponent} from "app/configuration/configuration.component";
import {canActivateHome} from "app/home/can-activate-home";
import {TrainingPeaksComponent} from "app/training-peaks/training-peaks.component";
import {TrainerRoadComponent} from "app/trainer-road/trainer-road.component";
import {MyFitnessPalComponent} from "app/myfitnesspal/myfitnesspal.component";
import {RouvyComponent} from "app/rouvy/rouvy.component";
import {LoginComponent} from "app/login/login.component";

export const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [canActivateHome]
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'training-peaks',
    component: TrainingPeaksComponent,
    canActivate: [canActivateHome]
  },
  {
    path: 'trainer-road',
    component: TrainerRoadComponent,
    canActivate: [canActivateHome]
  },
  {
    path: 'myfitnesspal',
    component: MyFitnessPalComponent,
    canActivate: [canActivateHome]
  },
  {
    path: 'rouvy',
    component: RouvyComponent,
    canActivate: [canActivateHome]
  },
  {
    path: 'config',
    component: ConfigurationComponent,
  },
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  }
];
