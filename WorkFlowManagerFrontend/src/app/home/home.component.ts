import { Component, OnInit } from '@angular/core';
import { LoginDialogOpenerService } from '../services/login/login-dialog-opener.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  constructor(private loginDialogOpener: LoginDialogOpenerService,
    private route: ActivatedRoute) {
      // intentionally empty
  }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      if(params.get("showLogin") === "true"){
        this.openDialog();
      }
    })
  }

  openDialog() {
    this.loginDialogOpener.open(true);
  }
  
}
