import { Request, Response, NextFunction } from 'express';
import { logger } from '../utils/logger';

export const errorHandler = (
  err: any,
  req: Request,
  res: Response,
  next: NextFunction
): void => {
  logger.error('Unhandled server error:', err);

  const statusCode = err.status || err.statusCode || 500;
  const message = err.message || 'An unexpected error occurred in the ship\'s engine room!';

  res.status(statusCode).json({
    error: message,
    status: statusCode
  });
};
