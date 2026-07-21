import { Request, Response, NextFunction } from 'express';
import { geminiService } from '../services/geminiService';
import { logger } from '../utils/logger';

export const handleChat = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  const { message, history, theme = 'onepiece' } = req.body;

  let retries = 2;
  while (retries >= 0) {
    try {
      const reply = await geminiService.generateResponse(message, history, theme);
      res.status(200).json(reply);
      return;
    } catch (error: any) {
      if (retries === 0) {
        logger.error('Failed to generate AI response after retries.', error);
        const errorMsg = theme === 'spiderman'
          ? 'Spider-Sense alert: unable to connect to financial servers right now. Please try again later!'
          : theme === 'basic'
            ? 'FinBot is temporarily unable to connect to analysis servers. Please try again shortly.'
            : 'The Finance Navigator is having trouble reading the stars right now. Please try again later!';
        res.status(500).json({ error: errorMsg });
        return;
      }
      logger.warn(`Gemini service failed. Retrying... (${retries} attempts left)`);
      retries--;
      // Wait 1 second before retrying
      await new Promise((resolve) => setTimeout(resolve, 1000));
    }
  }
};

export const handleReset = (req: Request, res: Response): void => {
  logger.info('Conversation reset requested.');
  const { theme = 'onepiece' } = req.body || {};
  const resetMsg = theme === 'spiderman'
    ? 'Case file history cleared. Spider-Man is ready for new missions.'
    : theme === 'basic'
      ? 'Chat history reset. FinBot is ready for your financial queries.'
      : 'Logbook cleared. The Navigation chat is reset.';
  res.status(200).json({ status: 'success', message: resetMsg });
};

export const handleHealth = (req: Request, res: Response): void => {
  res.status(200).json({
    status: 'healthy',
    uptime: process.uptime(),
    timestamp: new Date().toISOString()
  });
};
