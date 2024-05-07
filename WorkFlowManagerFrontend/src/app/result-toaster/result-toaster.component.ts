import { Component, Inject } from '@angular/core';
import { MAT_SNACK_BAR_DATA } from '@angular/material/snack-bar';

@Component({
  selector: 'app-result-toaster',
  templateUrl: './result-toaster.component.html',
  styleUrl: './result-toaster.component.css'
})
export class ResultToasterComponent {
  constructor(@Inject(MAT_SNACK_BAR_DATA) public data: any) { }
}
