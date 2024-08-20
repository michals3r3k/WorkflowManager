import { Component, Input, OnInit } from '@angular/core';
import { OrganizationMemberInvitationStatus } from './organization-details.component';

@Component({
  selector: 'member-delete-button',
  template: `
    <button mat mat-raised-button class="custom-chip" [color]="chipColor" (mouseenter)="onMouseEnter()" (mouseleave)="onMouseLeave()">
      <div class="chip-text-wrapper">
        <span class="chip-text default-label" [ngClass]="{ 'hidden': isHovered }">{{label}}</span>
        <span class="chip-text hover-label" [ngClass]="{ 'visible': isHovered }">{{hoverLabel}}</span>
      </div>
    </button>
  `,
  styles: `
    .custom-chip {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      height: 32px;
      overflow: hidden;
    }

    .chip-text-wrapper {
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;
      width: 60px;
    }

    .chip-text {
      position: absolute;
      transition: transform 0.3s ease, opacity 0.3s ease;
    }

    .default-label {
      transform: translateY(0);
      opacity: 1;
    }

    .hover-label {
      transform: translateY(100%);
      opacity: 0;
    }

    .custom-chip .hover-label.visible {
      transform: translateY(0);
      opacity: 1;
    }

    .custom-chip .default-label.hidden {
      transform: translateY(-100%);
      opacity: 0;
    }
  `
})
export class MemberDeleteButton implements OnInit {
  @Input() status: OrganizationMemberInvitationStatus;
  label: string;
  hoverLabel: string;
  isHovered: boolean = false;  // Flaga, aby śledzić stan hover
  chipColor: string;

  constructor() {}

  ngOnInit(): void {
    if(this.status == OrganizationMemberInvitationStatus.INVITED) {
      this.hoverLabel = 'Cancel';
    }
    else if(this.status == OrganizationMemberInvitationStatus.ACCEPTED) {
      this.hoverLabel = 'Delete';
    }
    else  {
      this.hoverLabel = 'Clear';
    }
    this.chipColor = 'primary'
    this.label = this.status.toString();
  }

  onMouseEnter() {
    this.chipColor = 'warn'
    this.isHovered = true;
  }

  onMouseLeave() {
    this.chipColor = 'primary'
    this.isHovered = false;
  }
  
}
