import { TestBed } from '@angular/core/testing';

import { LoginDialogOpenerService } from './login-dialog-opener.service';

describe('LoginDialogOpenerService', () => {
  let service: LoginDialogOpenerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoginDialogOpenerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
