import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import chatRoutes from './routes/chatRoutes';
import { rateLimiter } from './middlewares/rateLimiter';
import { errorHandler } from './middlewares/errorMiddleware';
import { config } from './config/gemini';
import { logger } from './utils/logger';
import * as authController from './controllers/authController';

const app = express();

// Trust proxy for Vercel deployment (express-rate-limit requirement)
app.set('trust proxy', 1);

// Security headers
app.use(helmet());

// Enable CORS for client apps
app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

// Rate limiter to prevent API key abuse
app.use('/api/', rateLimiter);

// JSON body parser
app.use(express.json());

// Auth & Email Verification Routes
app.post('/api/auth/register', authController.register);
app.post('/api/auth/verify-email', authController.verifyEmail);
app.post('/api/auth/login', authController.login);
app.post('/api/auth/resend-verification', authController.resendVerification);

// Main application routes
app.use('/api', chatRoutes);

// Global unhandled error handler
app.use(errorHandler);

// Start listening
const PORT = config.port || 3000;
app.listen(PORT, () => {
  logger.info(`Finance Navigator Backend is running on port ${PORT}`);
  if (process.env.RESEND_API_KEY) {
    logger.info('Resend Email Verification Integration configured successfully.');
  } else {
    logger.warn('WARNING: RESEND_API_KEY is missing. Verification emails will fail.');
  }
});

export default app;
