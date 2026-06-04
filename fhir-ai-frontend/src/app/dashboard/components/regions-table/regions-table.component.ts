import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { RegionData } from '../../../core/models/region.model';
import { SimilarRegionData } from '../../../core/models/similar-region-data.model';

@Component({
  selector: 'app-regions-table',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NzCardModule, NzTableModule, NzTagModule],
  templateUrl: './regions-table.component.html',
  styleUrls: ['./regions-table.component.css'],
})
export class RegionsTableComponent {

  regions = input<RegionData[]>([]);
  similarRegions = input<SimilarRegionData[]>([]);
  topRegion = computed(() => this.regions()[0] ?? null);

  similarityColor(similarity?: number): string {
    if (!similarity) return '#8b949e';
    if (similarity >= 0.85) return '#ff4d4f';
    if (similarity >= 0.70) return '#ff7a45';
    return '#faad14';
  }

  similarityTagColor(similarity?: number): string {
    if (!similarity) return 'default';
    if (similarity >= 0.85) return 'red';
    if (similarity >= 0.70) return 'orange';
    return 'gold';
  }

}