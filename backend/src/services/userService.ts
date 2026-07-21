export interface UserRecord {
  id: string;
  email: string;
  passwordHash: string;
  name: string;
  isVerified: boolean;
  verificationCodeHash?: string;
  verificationCodeExpiresAt?: number;
  verificationAttempts: number;
  lastResendAt?: number;
  createdAt: string;
}

export class UserStore {
  private static users = new Map<string, UserRecord>();

  static findByEmail(email: string): UserRecord | undefined {
    const target = email.toLowerCase().trim();
    for (const u of this.users.values()) {
      if (u.email === target) return u;
    }
    return undefined;
  }

  static findById(id: string): UserRecord | undefined {
    return this.users.get(id);
  }

  static save(user: UserRecord): UserRecord {
    this.users.set(user.id, user);
    return user;
  }
}
