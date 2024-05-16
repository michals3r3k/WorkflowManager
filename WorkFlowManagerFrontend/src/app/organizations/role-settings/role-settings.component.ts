import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-role-settings',
  templateUrl: './role-settings.component.html',
  styleUrls: ['./role-settings.component.css']
})
export class RoleSettingsComponent implements OnInit {

  disableBtnEnable = true;
  deleteBtnHover = false;
  timerID: any;
  searchUser = "";

  @Output() onClose : EventEmitter<null> = new EventEmitter();

  constructor() { }

  ngOnInit() {
  }

  close() {
    this.onClose.emit();
  }

  startDeleteBtnTimer() {
    this.deleteBtnHover = true;
    this.timerID = setTimeout(() => {
      this.disableBtnEnable = false;
    }, 2400);
  }

  resetDeleteBtnTimer() {
    this.deleteBtnHover = false;
    clearTimeout(this.timerID);
    this.disableBtnEnable = true;
  }

}
