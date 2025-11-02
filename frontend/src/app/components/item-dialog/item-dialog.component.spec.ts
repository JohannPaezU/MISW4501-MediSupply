import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ItemDialogComponent } from './item-dialog.component';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

describe('ItemDialogComponent', () => {
  let component: ItemDialogComponent;
  let fixture: ComponentFixture<ItemDialogComponent>;
  let el: DebugElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ItemDialogComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ItemDialogComponent);
    component = fixture.componentInstance;
    el = fixture.debugElement;
  });

  function triggerChangeDetection() {
    component.ngOnChanges({
      data: {
        currentValue: component.data,
        previousValue: null,
        firstChange: true,
        isFirstChange: () => true
      }
    });
    fixture.detectChanges();
  }

  it('debería crearse', () => {
    expect(component).toBeTruthy();
  });

  it('debería renderizar datos planos', () => {
    component.visible = true;
    component.data = { id: '1', name: 'Producto A', stock: 1200 };
    triggerChangeDetection();

    const rows = el.queryAll(By.css('tbody tr'));
    expect(rows.length).toBe(3);
    expect(rows[0].nativeElement.textContent).toContain('ID');
    expect(rows[1].nativeElement.textContent).toContain('Nombre');
    expect(rows[2].nativeElement.textContent).toContain('1.200');
  });


  it('debería manejar fechas correctamente', () => {
    component.visible = true;
    const fecha = '2025-10-31T10:00:00Z';
    component.data = { created_at: fecha, updated_at: fecha, due_date: fecha };
    triggerChangeDetection();

    component.displayData.forEach(item => {
      expect(item.value).toMatch(/\d{2}\/\d{2}\/\d{4}/);
    });
  });

  it('debería formatear precios', () => {
    component.visible = true;
    component.data = { price_per_unit: 25000, product: { price_per_unit: 1234.56 } };
    triggerChangeDetection();

    const price1 = component.displayData.find(d => d.label === 'Precio por unidad');
    const price2 = component.displayData.find(d => d.label === 'Precio por unidad'); // objeto anidado

    expect(price1?.value).toContain('$');
    expect(price2?.value).toContain('$');
  });

  it('debería formatear stock', () => {
    component.visible = true;
    component.data = { stock: 1234567 };
    triggerChangeDetection();

    const stock = component.displayData.find(d => d.label === 'Stock');
    expect(stock?.value).toBe('1.234.567');
  });

  it('debería manejar valores nulos o indefinidos', () => {
    component.visible = true;
    component.data = { id: null, name: undefined };
    triggerChangeDetection();

    component.displayData.forEach(item => {
      expect(item.value).toBe('N/A');
    });
  });

  it('debería traducir campos usando fieldTranslations', () => {
    expect(component['translateField']('seller.full_name')).toBe('Vendedor');
    expect(component['translateField']('product.id')).toBe('ID Producto');
  });

  it('debería usar último segmento si no hay traducción', () => {
    expect(component['translateField']('unknown.field')).toBe('Field');
  });

  it('debería formatear etiquetas dinámicas', () => {
    expect(component['formatLabel']('custom_field_name')).toBe('Custom Field Name');
  });

  it('debería emitir evento close al llamar onClose()', () => {
    spyOn(component.close, 'emit');
    component.onClose();
    expect(component.close.emit).toHaveBeenCalled();
  });

  it('debería mostrar mensaje vacío si no hay datos', () => {
    component.visible = true;
    component.data = {};
    triggerChangeDetection();

    const empty = el.query(By.css('.empty'));
    expect(empty).toBeTruthy();
    expect(empty.nativeElement.textContent).toContain('No hay información disponible');
  });

  it('debería ignorar propiedades heredadas en flattenObject', () => {
    const obj = Object.create({ inherited: 'shouldIgnore' });
    obj.own = 'ownValue';

    const result = (component as any).flattenObject(obj);
    expect(result.some((r: any) => r.label.includes('Inherited'))).toBeFalse();
    expect(result.some((r: any) => r.value === 'ownValue')).toBeTrue();
  });
});
