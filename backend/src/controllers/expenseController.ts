import { Response } from 'express';
import { AuthenticatedRequest } from '../middleware/authMiddleware';
import { MemoryStore } from '../services/dbService';
import { broadcastToGroup, broadcastToUser } from '../services/socketService';

export const createExpense = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const createdById = req.user?.userId;
    const { groupId, description, totalAmount, currency, category, paidById, splitType, splits, notes } = req.body;

    if (!description || !totalAmount || !paidById) {
      return res.status(400).json({ error: 'Description, totalAmount, and paidById are required' });
    }

    const expenseId = `exp_${Date.now()}`;
    const newExpense = {
      id: expenseId,
      groupId: groupId || null,
      description,
      totalAmount: parseFloat(totalAmount),
      currency: currency || 'USD',
      category: category || 'General',
      paidById,
      splitType: splitType || 'EQUAL',
      notes: notes || '',
      createdById,
      createdAt: new Date().toISOString()
    };

    MemoryStore.expenses.set(expenseId, newExpense);

    // Save splits
    if (Array.isArray(splits)) {
      splits.forEach((s: any) => {
        const splitRecordId = `es_${expenseId}_${s.userId}`;
        MemoryStore.expenseSplits.set(splitRecordId, {
          id: splitRecordId,
          expenseId,
          userId: s.userId,
          owedAmount: parseFloat(s.owedAmount || 0),
          percentage: s.percentage ? parseFloat(s.percentage) : null,
          shareCount: s.shareCount ? parseFloat(s.shareCount) : null
        });
      });
    }

    // Broadcast
    if (groupId) {
      broadcastToGroup(groupId, 'expense:created', { expense: newExpense, splits });
    } else if (Array.isArray(splits)) {
      splits.forEach((s: any) => broadcastToUser(s.userId, 'expense:created', { expense: newExpense, splits }));
    }

    return res.status(201).json({ expense: newExpense, splits });
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};

export const getExpensesByGroup = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const { groupId } = req.params;
    const expenses = Array.from(MemoryStore.expenses.values()).filter(e => e.groupId === groupId);

    const fullExpenses = expenses.map(exp => {
      const splits = Array.from(MemoryStore.expenseSplits.values()).filter(s => s.expenseId === exp.id);
      const paidBy = MemoryStore.users.get(exp.paidById);
      return {
        ...exp,
        paidByName: paidBy ? paidBy.name : exp.paidById,
        splits
      };
    });

    return res.status(200).json(fullExpenses);
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};

export const deleteExpense = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const { id } = req.params;
    const expense = MemoryStore.expenses.get(id);
    if (!expense) {
      return res.status(404).json({ error: 'Expense not found' });
    }

    MemoryStore.expenses.delete(id);
    // Delete splits
    Array.from(MemoryStore.expenseSplits.keys())
      .filter(key => key.startsWith(`es_${id}_`))
      .forEach(key => MemoryStore.expenseSplits.delete(key));

    if (expense.groupId) {
      broadcastToGroup(expense.groupId, 'expense:deleted', { expenseId: id });
    }

    return res.status(200).json({ message: 'Expense deleted successfully' });
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};
