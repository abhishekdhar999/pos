import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientsDashboard } from './clients-dashboard';

describe('ClientsDashboard', () => {
  let component: ClientsDashboard;
  let fixture: ComponentFixture<ClientsDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientsDashboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientsDashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
