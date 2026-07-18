import rateLimit from 'express-rate-limit';

export const rateLimiter = rateLimit({
  windowMs: 60 * 1000, // 1 minute window
  max: 30, // Limit each IP to 30 requests per minute
  message: {
    error: 'Too many navigation requests, matey! Please rest a bit before asking the Navigator again.'
  },
  standardHeaders: true,
  legacyHeaders: false,
});
