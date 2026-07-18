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
        res.status(500).json({
          error: 'The Finance Navigator is having trouble reading the stars right now. Please try again later!'
        });
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
  res.status(200).json({ status: 'success', message: 'Logbook cleared. The Navigation chat is reset.' });
};

export const handleHealth = (req: Request, res: Response): void => {
  res.status(200).json({
    status: 'healthy',
    uptime: process.uptime(),
    timestamp: new Date().toISOString()
  });
};
