import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrganizationMemberPickerComponent } from './organization-member-picker.component';

describe('OrganizationMemberPickerComponent', () => {
  let component: OrganizationMemberPickerComponent;
  let fixture: ComponentFixture<OrganizationMemberPickerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OrganizationMemberPickerComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OrganizationMemberPickerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
