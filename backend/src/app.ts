import express from 'express';
import http from 'http';
import cors from 'cors';
import helmet from 'helmet';
import chatRoutes from './routes/chatRoutes';
import { rateLimiter } from './middlewares/rateLimiter';
import { errorHandler } from './middlewares/errorMiddleware';
import { config } from './config/gemini';
import { logger } from './utils/logger';
import { initSocketServer } from './services/socketService';
import { authenticateToken } from './middleware/authMiddleware';

import * as authController from './controllers/authController';
import * as userController from './controllers/userController';
import * as friendController from './controllers/friendController';
import * as groupController from './controllers/groupController';
import * as expenseController from './controllers/expenseController';
import * as balanceController from './controllers/balanceController';
import * as settlementController from './controllers/settlementController';

const app = express();
const server = http.createServer(app);

// Trust proxy for Vercel deployment
app.set('trust proxy', 1);

// Security & CORS
app.use(helmet());
app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

app.use('/api/', rateLimiter);
app.use(express.json());

// Initialize Socket.IO
initSocketServer(server);

// --- Auth Routes ---
app.post('/api/auth/register', authController.register);
app.post('/api/auth/login', authController.login);
app.post('/api/auth/refresh', authController.refreshToken);
app.post('/api/auth/logout', authController.logout);

// --- User Routes ---
app.get('/api/users/me', authenticateToken, userController.getProfile);
app.put('/api/users/profile', authenticateToken, userController.updateProfile);
app.get('/api/users/search', authenticateToken, userController.searchUsers);

// --- Friends Routes ---
app.get('/api/friends', authenticateToken, friendController.getFriends);
app.post('/api/friends/request', authenticateToken, friendController.sendFriendRequest);
app.post('/api/friends/accept', authenticateToken, friendController.acceptFriendRequest);
app.post('/api/friends/reject', authenticateToken, friendController.rejectFriendRequest);
app.get('/api/friends/pending', authenticateToken, friendController.getPendingRequests);

// --- Group Routes ---
app.post('/api/groups', authenticateToken, groupController.createGroup);
app.get('/api/groups', authenticateToken, groupController.getGroups);
app.get('/api/groups/:id', authenticateToken, groupController.getGroupById);
app.post('/api/groups/:id/invite', authenticateToken, groupController.inviteMember);

// --- Expense Routes ---
app.post('/api/expenses', authenticateToken, expenseController.createExpense);
app.get('/api/groups/:groupId/expenses', authenticateToken, expenseController.getExpensesByGroup);
app.delete('/api/expenses/:id', authenticateToken, expenseController.deleteExpense);

// --- Balance & Settlement Routes ---
app.get('/api/groups/:groupId/balances', authenticateToken, balanceController.getBalancesByGroup);
app.post('/api/settlements', authenticateToken, settlementController.createSettlement);
app.get('/api/settlements', authenticateToken, settlementController.getSettlements);

// --- Existing Chat Routes ---
app.use('/api', chatRoutes);

// Error Handling
app.use(errorHandler);

const PORT = config.port || 3000;
server.listen(PORT, () => {
  logger.info(`Finance Guide Server (HTTP & WebSockets) running on port ${PORT}`);
});

export default app;
