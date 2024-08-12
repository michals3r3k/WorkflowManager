import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrganizationFilteringSelectComponent } from './organization-filtering-select.component';

describe('OrganizationFilteringSelectComponent', () => {
  let component: OrganizationFilteringSelectComponent;
  let fixture: ComponentFixture<OrganizationFilteringSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OrganizationFilteringSelectComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OrganizationFilteringSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
