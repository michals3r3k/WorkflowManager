import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'delete-button',
  template: `
    <button class="delete-btn" [disabled]="disableBtnEnable" (mouseenter)="startDeleteBtnTimer()" (mouseleave)="resetDeleteBtnTimer()">
    <div class="delete">
      <div class="delete-timer" [ngClass]="{'delete-timer-hover': deleteBtnHover}">
        <span class="timer-text" [ngClass]="{'timer-text-hover': deleteBtnHover}"></span>
      </div>
      <mat-icon>delete</mat-icon>
    </div>
  </button>
  `,
  styles: `
    .delete {
        display: flex;
        justify-content: flex-end;
        padding: 3px;
        font-weight: 700;
    }

    .delete mat-icon {
        min-width: 24px;
    }

    .delete-btn {
        background: #FF2400;
        color: #fff;
        padding: 0;
        border: none;
        border-radius: 10px;
        width: 30px;
        height: 30px;
        margin: 0 10px;
        transition: all 0.3s ease;
    }

    .delete-btn:disabled {
        background: #9993;
        color: #000;
    }

    .delete-btn:hover {
        width: 90px;
    }

    .delete-timer {
        opacity: 0;
        width: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
        transition: all 0.3s ease;
    }

    .delete-timer-hover {
        opacity: 1;
    }

    .timer-text::after {
        content: "3";
    }

    .timer-text-hover::after {
        animation: countdown 3s linear forwards;
    }

    @keyframes countdown {
        0% {
            content: "3"
        }
        33% {
            content: "2"
        }
        66% {
            content: "1"
        }
        100% {
            content: "DELETE"
        }
    }
  `
})
export class DeleteButton implements OnInit {
    disableBtnEnable = true;
    deleteBtnHover = false;
    timerID: any;

  constructor() {}

  ngOnInit(): void { }

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
