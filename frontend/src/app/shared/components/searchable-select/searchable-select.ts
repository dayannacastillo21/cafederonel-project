import {
  Component,
  ElementRef,
  HostListener,
  Input,
  computed,
  forwardRef,
  inject,
  signal,
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-searchable-select',
  imports: [],
  templateUrl: './searchable-select.html',
  styleUrl: './searchable-select.scss',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SearchableSelectComponent),
      multi: true,
    },
  ],
})
export class SearchableSelectComponent implements ControlValueAccessor {
  private readonly host = inject(ElementRef<HTMLElement>);

  @Input({ required: true }) options: readonly string[] = [];
  @Input() searchPlaceholder = 'Buscar...';
  @Input() emptyLabel = 'Seleccionar';
  @Input() invalid = false;

  protected readonly open = signal(false);
  protected readonly searchTerm = signal('');
  protected readonly disabled = signal(false);

  protected readonly filteredOptions = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    if (!term) {
      return this.options;
    }

    return this.options.filter((option) => option.toLowerCase().includes(term));
  });

  private selectedValue = '';
  private onChange: (value: string) => void = () => undefined;
  private onTouched: () => void = () => undefined;

  protected displayValue(): string {
    return this.selectedValue || this.emptyLabel;
  }

  protected hasSelection(): boolean {
    return Boolean(this.selectedValue);
  }

  protected selectedLabel(): string {
    return this.selectedValue;
  }

  protected togglePanel(): void {
    if (this.disabled()) {
      return;
    }

    const next = !this.open();
    this.open.set(next);
    if (!next) {
      this.searchTerm.set('');
      return;
    }

    queueMicrotask(() => {
      const input = this.host.nativeElement.querySelector('.search-box input') as HTMLInputElement | null;
      input?.focus();
    });
  }

  protected updateSearch(event: Event): void {
    this.searchTerm.set((event.target as HTMLInputElement).value);
  }

  protected selectOption(option: string): void {
    this.selectedValue = option;
    this.onChange(option);
    this.onTouched();
    this.open.set(false);
    this.searchTerm.set('');
  }

  protected clearSearch(event: Event): void {
    event.stopPropagation();
    this.searchTerm.set('');
  }

  writeValue(value: string | null): void {
    this.selectedValue = value ?? '';
  }

  registerOnChange(fn: (value: string) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled.set(isDisabled);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.open()) {
      return;
    }

    const target = event.target as Node | null;
    if (target && !this.host.nativeElement.contains(target)) {
      this.open.set(false);
      this.searchTerm.set('');
      this.onTouched();
    }
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (!this.open()) {
      return;
    }

    this.open.set(false);
    this.searchTerm.set('');
    this.onTouched();
  }
}
