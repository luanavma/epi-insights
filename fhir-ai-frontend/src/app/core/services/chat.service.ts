import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AskRequest, AskResponse } from '../models/chat.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ChatService {

  private readonly http = inject(HttpClient);
  private readonly chatId = crypto.randomUUID();
  private readonly apiUrl =  environment.apiUrl + '/fhir-agent'; 

  ask(question: string): Observable<AskResponse> {
    const body: AskRequest = {
      chatId: this.chatId,
      question
    };
    return this.http.post<AskResponse>(`${this.apiUrl}/ask`, body);
  }
}