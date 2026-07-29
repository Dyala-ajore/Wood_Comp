import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-brand-panel',
  standalone: true,
  templateUrl: './brand-panel.component.html',
  styleUrl: './brand-panel.component.scss',
})
export class BrandPanelComponent {
  @Input() eyebrow = '';
  @Input() title = '';
  @Input() tagline = '';

  /** علامات شريط القياس العمودي — من 0 إلى 200 سم بفواصل 10 */
  readonly ticks = Array.from({ length: 21 }, (_, i) => i * 10);
}
