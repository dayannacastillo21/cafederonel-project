export type LoginRequest = {
  email: string;
  password: string;
};

export type LoginResponse = {
  userId: number;
  userName: string;
  email: string;
  role: 'ADMIN' | 'CAJERO' | 'INVENTARIO' | 'CONTADOR';
  token: string;
};

export type SessionUser = Omit<LoginResponse, 'token'>;
