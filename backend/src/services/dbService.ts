import { PrismaClient } from '@prisma/client';

export const prisma = new PrismaClient();

// In-Memory Fallback Storage when PostgreSQL database is not connected
export class MemoryStore {
  static users: Map<string, any> = new Map();
  static sessions: Map<string, any> = new Map();
  static friendships: Map<string, any> = new Map();
  static friendRequests: Map<string, any> = new Map();
  static groups: Map<string, any> = new Map();
  static groupMembers: Map<string, any> = new Map();
  static expenses: Map<string, any> = new Map();
  static expenseSplits: Map<string, any> = new Map();
  static settlements: Map<string, any> = new Map();
  static notifications: Map<string, any> = new Map();
}
