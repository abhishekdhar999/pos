import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductsDashboard } from './products-dashboard';

describe('ProductsDashboard', () => {
  let component: ProductsDashboard;
  let fixture: ComponentFixture<ProductsDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductsDashboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductsDashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
