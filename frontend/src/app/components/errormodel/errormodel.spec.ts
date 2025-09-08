import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Errormodel } from './errormodel';

describe('Errormodel', () => {
  let component: Errormodel;
  let fixture: ComponentFixture<Errormodel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Errormodel]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Errormodel);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
