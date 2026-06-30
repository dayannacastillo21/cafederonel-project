import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { map, tap } from 'rxjs';

import { API_BASE_URL } from '../api/api.config';
import { LoginRequest, LoginResponse, SessionUser } from '../../models/auth.models';

const SESSION_STORAGE_KEY = 'cafederonel_session';

type StoredSession = LoginResponse;

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  readonly session = signal<SessionUser | null>(this.readSessionUser());

  login(credentials: LoginRequest) {
    return this.http.post<LoginResponse>(`${API_BASE_URL}/auth/sesiones`, credentials).pipe(
      tap((response) => this.storeSession(response)),
      map(({ token: _token, ...user }) => user),
    );
  }

  token(): string | null {
    return this.readStoredSession()?.token ?? null;
  }

  isAuthenticated(): boolean {
    return Boolean(this.token());
  }

  logout(): void {
    if (this.canUseLocalStorage()) {
      localStorage.removeItem(SESSION_STORAGE_KEY);
    }
    this.session.set(null);
    void this.router.navigateByUrl('/login');
  }

  private storeSession(response: LoginResponse): void {
    if (this.canUseLocalStorage()) {
      localStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(response));
    }
    const { token: _token, ...user } = response;
    this.session.set(user);
  }

  private readSessionUser(): SessionUser | null {
    const stored = this.readStoredSession();
    if (!stored) {
      return null;
    }
    const { token: _token, ...user } = stored;
    return user;
  }

  private readStoredSession(): StoredSession | null {
    if (!this.canUseLocalStorage()) {
      return null;
    }

    const raw = localStorage.getItem(SESSION_STORAGE_KEY);
    if (!raw) {
      return null;
    }

    try {
      return JSON.parse(raw) as StoredSession;
    } catch {
      localStorage.removeItem(SESSION_STORAGE_KEY);
      return null;
    }
  }

  private canUseLocalStorage(): boolean {
    return typeof localStorage !== 'undefined';
  }
}
