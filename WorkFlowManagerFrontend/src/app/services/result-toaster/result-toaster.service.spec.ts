import { TestBed } from '@angular/core/testing';

import { ResultToasterService } from './result-toaster.service';

describe('ResultToasterService', () => {
  let service: ResultToasterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ResultToasterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
