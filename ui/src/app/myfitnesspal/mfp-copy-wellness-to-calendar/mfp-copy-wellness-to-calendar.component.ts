import {Component, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {MatSelectModule} from "@angular/material/select";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {Platform} from "integration/platform";
import {MatListModule} from "@angular/material/list";
import {
  CopyWellnessToCalendarComponent
} from "app/components/copy-wellness-to-calendar/copy-wellness-to-calendar.component";

@Component({
  selector: 'mfp-copy-wellness-to-calendar',
  standalone: true,
  imports: [
    FormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
    MatProgressBarModule,
    MatDatepickerModule,
    MatSnackBarModule,
    MatSelectModule,
    MatCheckboxModule,
    MatListModule,
    CopyWellnessToCalendarComponent,
  ],
  templateUrl: './mfp-copy-wellness-to-calendar.component.html',
  styleUrl: './mfp-copy-wellness-to-calendar.component.scss'
})
export class MfpCopyWellnessToCalendarComponent implements OnInit {
  readonly Platform = Platform;
  readonly directions = [
    {title: "MyFitnessPal -> Intervals.icu", value: Platform.DIRECTION_MFP_INT},
  ]
  readonly wellnessTypes = [
    {title: "Weight", value: "WEIGHT"},
    {title: "Calories", value: "CALORIES"},
    {title: "Carbohydrates", value: "CARBOHYDRATES"},
    {title: "Protein", value: "PROTEIN"},
    {title: "Fat", value: "FAT"},
  ]
  readonly selectedWellnessTypes = ['WEIGHT', 'CALORIES', 'CARBOHYDRATES', 'PROTEIN', 'FAT']

  constructor() {
  }

  ngOnInit(): void {
  }
}
