import { inject, Injectable } from "@angular/core";
import { SimilarRegionData } from "../models/similar-region-data.model";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../../environments/environment";
import { Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class RegionService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl =  environment.apiUrl + '/regions'; 
  
  findSimilarRegions(city: string, state: string): Observable<SimilarRegionData[]> {
    return this.http.get<SimilarRegionData[]>(`${this.apiUrl}/similar?city=${city}&state=${state}`);
  }

}