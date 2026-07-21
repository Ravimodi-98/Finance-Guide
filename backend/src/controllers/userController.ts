import { Response } from 'express';
import { AuthenticatedRequest } from '../middleware/authMiddleware';
import { MemoryStore } from '../services/dbService';

export const getProfile = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const userId = req.user?.userId;
    const user = MemoryStore.users.get(userId || '');
    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }
    const { passwordHash, ...userWithoutPassword } = user;
    return res.status(200).json(userWithoutPassword);
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};

export const updateProfile = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const userId = req.user?.userId;
    const user = MemoryStore.users.get(userId || '');
    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }

    const { name, phone, themePreference, avatarUrl } = req.body;
    if (name) user.name = name;
    if (phone) user.phone = phone;
    if (themePreference) user.themePreference = themePreference;
    if (avatarUrl) user.avatarUrl = avatarUrl;

    const { passwordHash, ...userWithoutPassword } = user;
    return res.status(200).json(userWithoutPassword);
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};

export const searchUsers = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const query = ((req.query.q as string) || '').toLowerCase();
    const currentUserId = req.user?.userId;

    const matchedUsers = Array.from(MemoryStore.users.values())
      .filter(u => u.id !== currentUserId && (u.email.includes(query) || u.name.toLowerCase().includes(query)))
      .map(({ passwordHash, ...u }) => u);

    return res.status(200).json(matchedUsers);
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};
