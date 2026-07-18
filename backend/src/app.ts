import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import chatRoutes from './routes/chatRoutes';
import { rateLimiter } from './middlewares/rateLimiter';
import { errorHandler } from './middlewares/errorMiddleware';
import { config } from './config/gemini';
import { logger } from './utils/logger';

const app = express();

// Trust proxy for Vercel deployment (express-rate-limit requirement)
app.set('trust proxy', 1);

// Security headers
app.use(helmet());

// Enable CORS for client apps
app.use(cors({
  origin: '*', // In production, customize this to restrict access to specific origins
  methods: ['GET', 'POST'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

// Rate limiter to prevent API key abuse
app.use('/api/', rateLimiter);

// JSON body parser
app.use(express.json());

// Main application routes
app.use('/api', chatRoutes);

// Global unhandled error handler
app.use(errorHandler);

// Start listening
const PORT = config.port;
app.listen(PORT, () => {
  logger.info(`Finance Navigator Backend is running on port ${PORT}`);
  if (config.geminiApiKey) {
    logger.info('Gemini API Integration is configured successfully.');
  } else {
    logger.warn('WARNING: Gemini API Key is missing. Operations will fail.');
  }
});

export default app;
