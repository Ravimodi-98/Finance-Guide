import { Response } from 'express';
import { AuthenticatedRequest } from '../middleware/authMiddleware';
import { MemoryStore } from '../services/dbService';
import { broadcastToUser } from '../services/socketService';

export const getFriends = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const userId = req.user?.userId;
    const friendships = Array.from(MemoryStore.friendships.values()).filter(
      f => f.user1Id === userId || f.user2Id === userId
    );

    const friendIds = friendships.map(f => (f.user1Id === userId ? f.user2Id : f.user1Id));
    const friends = friendIds
      .map(id => MemoryStore.users.get(id))
      .filter(Boolean)
      .map(({ passwordHash, ...u }) => u);

    return res.status(200).json(friends);
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};

export const sendFriendRequest = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const senderId = req.user?.userId;
    const { receiverId } = req.body;

    if (!receiverId || receiverId === senderId) {
      return res.status(400).json({ error: 'Invalid receiver ID' });
    }

    const requestId = `req_${Date.now()}`;
    const newReq = { id: requestId, senderId, receiverId, status: 'PENDING', createdAt: new Date().toISOString() };
    MemoryStore.friendRequests.set(requestId, newReq);

    broadcastToUser(receiverId, 'friend:request', newReq);

    return res.status(201).json(newReq);
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};

export const acceptFriendRequest = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const userId = req.user?.userId;
    const { requestId } = req.body;

    const request = MemoryStore.friendRequests.get(requestId);
    if (!request || request.receiverId !== userId) {
      return res.status(404).json({ error: 'Friend request not found' });
    }

    request.status = 'ACCEPTED';
    const friendshipId = `f_${Date.now()}`;
    const newFriendship = {
      id: friendshipId,
      user1Id: request.senderId,
      user2Id: request.receiverId,
      createdAt: new Date().toISOString()
    };
    MemoryStore.friendships.set(friendshipId, newFriendship);

    broadcastToUser(request.senderId, 'friend:accepted', newFriendship);

    return res.status(200).json({ message: 'Friend request accepted', friendship: newFriendship });
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};

export const rejectFriendRequest = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const { requestId } = req.body;
    const request = MemoryStore.friendRequests.get(requestId);
    if (request) {
      request.status = 'REJECTED';
    }
    return res.status(200).json({ message: 'Friend request rejected' });
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};

export const getPendingRequests = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const userId = req.user?.userId;
    const pending = Array.from(MemoryStore.friendRequests.values()).filter(
      r => r.receiverId === userId && r.status === 'PENDING'
    );
    return res.status(200).json(pending);
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};
