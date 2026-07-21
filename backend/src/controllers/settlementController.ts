import { Response } from 'express';
import { AuthenticatedRequest } from '../middleware/authMiddleware';
import { MemoryStore } from '../services/dbService';
import { broadcastToGroup, broadcastToUser } from '../services/socketService';

export const createSettlement = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const payerId = req.user?.userId;
    const { groupId, payeeId, amount, currency, note } = req.body;

    if (!payeeId || !amount) {
      return res.status(400).json({ error: 'Payee ID and amount are required' });
    }

    const settlementId = `stl_${Date.now()}`;
    const newSettlement = {
      id: settlementId,
      groupId: groupId || null,
      payerId: payerId || '',
      payeeId,
      amount: parseFloat(amount),
      currency: currency || 'USD',
      note: note || '',
      settledAt: new Date().toISOString()
    };

    MemoryStore.settlements.set(settlementId, newSettlement);

    if (groupId) {
      broadcastToGroup(groupId, 'settlement:created', newSettlement);
    }
    broadcastToUser(payeeId, 'settlement:created', newSettlement);

    return res.status(201).json(newSettlement);
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};

export const getSettlements = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const userId = req.user?.userId;
    const mySettlements = Array.from(MemoryStore.settlements.values()).filter(
      s => s.payerId === userId || s.payeeId === userId
    );

    const fullSettlements = mySettlements.map(s => {
      const payer = MemoryStore.users.get(s.payerId);
      const payee = MemoryStore.users.get(s.payeeId);
      return {
        ...s,
        payerName: payer ? payer.name : s.payerId,
        payeeName: payee ? payee.name : s.payeeId
      };
    });

    return res.status(200).json(fullSettlements);
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};
