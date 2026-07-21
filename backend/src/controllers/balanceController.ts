import { Response } from 'express';
import { AuthenticatedRequest } from '../middleware/authMiddleware';
import { MemoryStore } from '../services/dbService';

export interface DebtTransaction {
  fromUserId: string;
  fromUserName: string;
  toUserId: string;
  toUserName: string;
  amount: number;
}

export const getBalancesByGroup = async (req: AuthenticatedRequest, res: Response) => {
  try {
    const { groupId } = req.params;

    // Get group expenses
    const expenses = Array.from(MemoryStore.expenses.values()).filter(e => e.groupId === groupId);
    const settlements = Array.from(MemoryStore.settlements.values()).filter(s => s.groupId === groupId);

    // Calculate net balances per user
    const netBalances = new Map<string, number>();

    expenses.forEach(exp => {
      const payerId = exp.paidById;
      const splits = Array.from(MemoryStore.expenseSplits.values()).filter(s => s.expenseId === exp.id);

      splits.forEach(split => {
        if (split.userId !== payerId) {
          // Payer is owed money (+), Split user owes money (-)
          netBalances.set(payerId, (netBalances.get(payerId) || 0) + split.owedAmount);
          netBalances.set(split.userId, (netBalances.get(split.userId) || 0) - split.owedAmount);
        }
      });
    });

    // Account for settlements
    settlements.forEach(s => {
      netBalances.set(s.payerId, (netBalances.get(s.payerId) || 0) + s.amount);
      netBalances.set(s.payeeId, (netBalances.get(s.payeeId) || 0) - s.amount);
    });

    // Simplify debts into minimum transactions algorithm
    const debtors: { userId: string; amount: number }[] = [];
    const creditors: { userId: string; amount: number }[] = [];

    netBalances.forEach((net, uId) => {
      if (net < -0.01) {
        debtors.push({ userId: uId, amount: -net });
      } else if (net > 0.01) {
        creditors.push({ userId: uId, amount: net });
      }
    });

    const simplifiedDebts: DebtTransaction[] = [];
    let i = 0;
    let j = 0;

    while (i < debtors.length && j < creditors.length) {
      const debtor = debtors[i];
      const creditor = creditors[j];

      const settledAmount = Math.min(debtor.amount, creditor.amount);

      const debtorUser = MemoryStore.users.get(debtor.userId);
      const creditorUser = MemoryStore.users.get(creditor.userId);

      simplifiedDebts.push({
        fromUserId: debtor.userId,
        fromUserName: debtorUser ? debtorUser.name : debtor.userId,
        toUserId: creditor.userId,
        toUserName: creditorUser ? creditorUser.name : creditor.userId,
        amount: Math.round(settledAmount * 100) / 100
      });

      debtor.amount -= settledAmount;
      creditor.amount -= settledAmount;

      if (debtor.amount < 0.01) i++;
      if (creditor.amount < 0.01) j++;
    }

    return res.status(200).json({
      groupId,
      simplifiedDebts,
      rawBalances: Object.fromEntries(netBalances)
    });
  } catch (error: any) {
    return res.status(500).json({ error: error.message });
  }
};
