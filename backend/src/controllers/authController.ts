import { Request, Response } from 'express';
import bcrypt from 'bcryptjs';
import { generateAccessToken, generateRefreshToken, verifyRefreshToken } from '../utils/jwt';
import { MemoryStore } from '../services/dbService';

export const register = async (req: Request, res: Response) => {
  try {
    const { email, password, name, phone, themePreference } = req.body;
    if (!email || !password || !name) {
      return res.status(400).json({ error: 'Email, password, and name are required' });
    }

    // Check existing
    const existing = Array.from(MemoryStore.users.values()).find(u => u.email === email.toLowerCase());
    if (existing) {
      return res.status(400).json({ error: 'User with this email already exists' });
    }

    const hashedPassword = await bcrypt.hash(password, 10);
    const userId = `user_${Date.now()}_${Math.floor(Math.random() * 1000)}`;
    const newUser = {
      id: userId,
      email: email.toLowerCase(),
      passwordHash: hashedPassword,
      name,
      phone: phone || null,
      avatarUrl: null,
      themePreference: themePreference || 'PRO',
      onlineStatus: true,
      createdAt: new Date().toISOString()
    };

    MemoryStore.users.set(userId, newUser);

    const accessToken = generateAccessToken({ userId, email: newUser.email });
    const refreshToken = generateRefreshToken({ userId, email: newUser.email });

    MemoryStore.sessions.set(refreshToken, { userId, refreshToken, expiresAt: Date.now() + 7 * 86400000 });

    const { passwordHash, ...userWithoutPassword } = newUser;
    return res.status(201).json({
      user: userWithoutPassword,
      accessToken,
      refreshToken
    });
  } catch (error: any) {
    return res.status(500).json({ error: error.message || 'Registration failed' });
  }
};

export const login = async (req: Request, res: Response) => {
  try {
    const { email, password } = req.body;
    if (!email || !password) {
      return res.status(400).json({ error: 'Email and password are required' });
    }

    const user = Array.from(MemoryStore.users.values()).find(u => u.email === email.toLowerCase());
    if (!user) {
      return res.status(401).json({ error: 'Invalid email or password' });
    }

    const isMatch = await bcrypt.compare(password, user.passwordHash);
    if (!isMatch) {
      return res.status(401).json({ error: 'Invalid email or password' });
    }

    user.onlineStatus = true;

    const accessToken = generateAccessToken({ userId: user.id, email: user.email });
    const refreshToken = generateRefreshToken({ userId: user.id, email: user.email });

    MemoryStore.sessions.set(refreshToken, { userId: user.id, refreshToken, expiresAt: Date.now() + 7 * 86400000 });

    const { passwordHash, ...userWithoutPassword } = user;
    return res.status(200).json({
      user: userWithoutPassword,
      accessToken,
      refreshToken
    });
  } catch (error: any) {
    return res.status(500).json({ error: error.message || 'Login failed' });
  }
};

export const refreshToken = async (req: Request, res: Response) => {
  try {
    const { refreshToken: token } = req.body;
    if (!token) {
      return res.status(400).json({ error: 'Refresh token required' });
    }

    const session = MemoryStore.sessions.get(token);
    if (!session) {
      return res.status(401).json({ error: 'Invalid or revoked refresh token' });
    }

    const payload = verifyRefreshToken(token);
    if (!payload) {
      MemoryStore.sessions.delete(token);
      return res.status(401).json({ error: 'Expired refresh token' });
    }

    // Token Rotation
    MemoryStore.sessions.delete(token);
    const newAccessToken = generateAccessToken({ userId: payload.userId, email: payload.email });
    const newRefreshToken = generateRefreshToken({ userId: payload.userId, email: payload.email });

    MemoryStore.sessions.set(newRefreshToken, { userId: payload.userId, refreshToken: newRefreshToken, expiresAt: Date.now() + 7 * 86400000 });

    return res.status(200).json({
      accessToken: newAccessToken,
      refreshToken: newRefreshToken
    });
  } catch (error: any) {
    return res.status(500).json({ error: error.message || 'Refresh failed' });
  }
};

export const logout = async (req: Request, res: Response) => {
  try {
    const { refreshToken: token } = req.body;
    if (token) {
      MemoryStore.sessions.delete(token);
    }
    return res.status(200).json({ message: 'Logged out successfully' });
  } catch (error: any) {
    return res.status(500).json({ error: error.message || 'Logout failed' });
  }
};
