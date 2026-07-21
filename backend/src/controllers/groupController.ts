import { Response } from 'express';
import { AuthenticatedRequest } from '../middleware/authMiddleware';
import { MemoryStore } from '../services/dbService';
import { broadcastToGroup, broadcastToUser } from '../services/socketService';

export const createGroup = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const userId = req.user?.userId;
    const { name, icon, description, memberIds } = req.body;

    if (!name) {
      return res.status(400).json({ error: 'Group name is required' });
    }

    const groupId = `grp_${Date.now()}`;
    const newGroup = {
      id: groupId,
      name,
      icon: icon || 'group',
      description: description || '',
      createdById: userId,
      createdAt: new Date().toISOString()
    };

    MemoryStore.groups.set(groupId, newGroup);

    // Add creator as ADMIN
    const allMemberIds = Array.from(new Set([userId, ...(memberIds || [])]));
    allMemberIds.forEach(mId => {
      const memberRecordId = `gm_${groupId}_${mId}`;
      MemoryStore.groupMembers.set(memberRecordId, {
        id: memberRecordId,
        groupId,
        userId: mId,
        role: mId === userId ? 'ADMIN' : 'MEMBER',
        joinedAt: new Date().toISOString()
      });
      broadcastToUser(mId as string, 'group:updated', newGroup);
    });

    return res.status(201).json(newGroup);
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};

export const getGroups = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const userId = req.user?.userId;
    const myGroupMemberships = Array.from(MemoryStore.groupMembers.values()).filter(gm => gm.userId === userId);
    const groupIds = myGroupMemberships.map(gm => gm.groupId);

    const userGroups = groupIds.map(id => MemoryStore.groups.get(id)).filter(Boolean);
    return res.status(200).json(userGroups);
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};

export const getGroupById = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const { id } = req.params;
    const group = MemoryStore.groups.get(id);
    if (!group) {
      return res.status(404).json({ error: 'Group not found' });
    }

    const members = Array.from(MemoryStore.groupMembers.values())
      .filter(gm => gm.groupId === id)
      .map(gm => {
        const u = MemoryStore.users.get(gm.userId);
        return {
          ...gm,
          user: u ? { id: u.id, name: u.name, email: u.email, avatarUrl: u.avatarUrl } : null
        };
      });

    return res.status(200).json({ ...group, members });
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};

export const inviteMember = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const { id } = req.params;
    const { userId: inviteeId } = req.body;

    const group = MemoryStore.groups.get(id);
    if (!group) {
      return res.status(404).json({ error: 'Group not found' });
    }

    const memberRecordId = `gm_${id}_${inviteeId}`;
    if (!MemoryStore.groupMembers.has(memberRecordId)) {
      MemoryStore.groupMembers.set(memberRecordId, {
        id: memberRecordId,
        groupId: id,
        userId: inviteeId,
        role: 'MEMBER',
        joinedAt: new Date().toISOString()
      });
      broadcastToUser(inviteeId, 'group:updated', group);
      broadcastToGroup(id, 'member:joined', { groupId: id, userId: inviteeId });
    }

    return res.status(200).json({ message: 'User invited to group successfully' });
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};
