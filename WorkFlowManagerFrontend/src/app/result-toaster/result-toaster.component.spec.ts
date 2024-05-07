import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResultToasterComponent } from './result-toaster.component';

describe('ResultToasterComponent', () => {
  let component: ResultToasterComponent;
  let fixture: ComponentFixture<ResultToasterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ResultToasterComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ResultToasterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
