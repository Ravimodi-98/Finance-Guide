import { Request, Response, NextFunction } from 'express';
import { logger } from '../utils/logger';

export const validateChatRequest = (req: Request, res: Response, next: NextFunction): void => {
  const { message, history } = req.body;

  if (message === undefined || typeof message !== 'string' || message.trim() === '') {
    res.status(400).json({ error: 'Message parameter is required and must be a non-empty string.' });
    return;
  }

  // Sanitize message: remove potential injection/malicious content
  req.body.message = message.trim();

  if (history !== undefined) {
    if (!Array.isArray(history)) {
      res.status(400).json({ error: 'History parameter must be an array of chat messages.' });
      return;
    }

    for (let i = 0; i < history.length; i++) {
      const item = history[i];
      if (
        !item ||
        typeof item !== 'object' ||
        (item.role !== 'user' && item.role !== 'model') ||
        typeof item.text !== 'string'
      ) {
        res.status(400).json({
          error: `History item at index ${i} is invalid. Each item must have 'role' ('user' | 'model') and 'text' properties.`
        });
        return;
      }
      // Trim histories
      item.text = item.text.trim();
    }
  } else {
    req.body.history = [];
  }

  next();
};
