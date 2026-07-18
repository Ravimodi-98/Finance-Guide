import { Router } from 'express';
import { handleChat, handleReset, handleHealth } from '../controllers/chatController';
import { validateChatRequest } from '../middlewares/validation';

const router = Router();

router.post('/chat', validateChatRequest, handleChat);
router.post('/chat/reset', handleReset);
router.get('/health', handleHealth);

export default router;
