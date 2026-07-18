import dotenv from 'dotenv';
import { logger } from '../utils/logger';

dotenv.config();

const apiKey = process.env.GEMINI_API_KEY;

if (!apiKey) {
  logger.error('GEMINI_API_KEY env variable is missing! The server might fail to answer requests.');
}

export const config = {
  geminiApiKey: apiKey || '',
  port: process.env.PORT || 3000
};
