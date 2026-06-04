import {
  ChangeDetectionStrategy, Component, input,
  effect, AfterViewInit, ElementRef, viewChild
} from '@angular/core';
import { NzCardModule } from 'ng-zorro-antd/card';
import * as L from 'leaflet';
import 'leaflet.heat';
import { RegionData } from '../../../core/models/region.model';

@Component({
  selector: 'app-heatmap',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NzCardModule],
  templateUrl: './heatmap.component.html',
})
export class HeatmapComponent implements AfterViewInit {

  regions = input<RegionData[]>([]);

  private mapEl = viewChild<ElementRef>('mapEl');
  private map!: L.Map;
  private heatLayer: L.Layer | null = null;
  private markersLayer: L.LayerGroup | null = null;

  constructor() {
    effect(() => {
      const data = this.regions();
      if (this.map && data.length) {
        this.updateHeatmap(data);
      }
    });
  }

  ngAfterViewInit() {
    setTimeout(() => this.initMap(), 100);
  }

  private initMap() {
    const el = this.mapEl()?.nativeElement;
    if (!el) return;

    this.map = L.map(el, {
      center: [-15.7801, -47.9292],
      zoom: 4,
    });

    L.tileLayer(
      'https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png',
      { attribution: '© OpenStreetMap © CARTO' }
    ).addTo(this.map);

    if (this.regions().length) {
      this.updateHeatmap(this.regions());
    }
  }

  private updateHeatmap(data: RegionData[]) {
    if (this.heatLayer) {
      this.map.removeLayer(this.heatLayer);
    }
    if (this.markersLayer) {
      this.map.removeLayer(this.markersLayer);
      this.markersLayer = null;
    }
    const max = Math.max(...data.map(r =>r.totalCases));

    const points = data.map(r => [
      r.latitude, r.longitude, r.totalCases / max
    ] as [number, number, number]);

    this.heatLayer = (L as unknown as Record<string, CallableFunction>)
      ['heatLayer'](points, {
        radius: 35, blur: 25,
        gradient: { 0.2: '#00ff00', 0.5: '#ffff00', 0.75: '#ff6600', 1.0: '#ff0000' },
        minOpacity: 0.8
      })
      .addTo(this.map);

    // Add markers with popups for each region
    this.markersLayer = L.layerGroup();
    data.forEach(r => {
      const marker = L.circleMarker([r.latitude, r.longitude], {
        radius: 8,
        fillColor: '#ff3333',
        color: '#ff3333',
        weight: 1,
        opacity: 1,
        fillOpacity: 0.9
      });
      const popup = `<strong>${r.city} - ${r.state}</strong><br/>Cases: ${r.totalCases}<br/>Symptoms: ${r.mainSymptoms}`;
      marker.bindPopup(popup);
      this.markersLayer!.addLayer(marker);
    });
    this.markersLayer.addTo(this.map);

    // Center map on region with most cases
    const maxRegion = data.reduce((prev, cur) => (cur.totalCases > prev.totalCases ? cur : prev), data[0]);
    if (maxRegion && maxRegion.latitude != null && maxRegion.longitude != null) {
      this.map.flyTo([maxRegion.latitude, maxRegion.longitude], 8, { duration: 0.8 });
    }
  }
}