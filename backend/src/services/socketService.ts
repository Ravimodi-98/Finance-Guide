import { Server as HttpServer } from 'http';
import { Server, Socket } from 'socket.io';
import { verifyAccessToken } from '../utils/jwt';

let io: Server | null = null;

export const initSocketServer = (httpServer: HttpServer) => {
  io = new Server(httpServer, {
    cors: {
      origin: '*',
      methods: ['GET', 'POST']
    }
  });

  io.use((socket: Socket, next: (err?: Error) => void) => {
    const token = socket.handshake.auth.token || socket.handshake.query.token;
    if (token) {
      const decoded = verifyAccessToken(token as string);
      if (decoded) {
        (socket as any).userId = decoded.userId;
        return next();
      }
    }
    // Allow connection even without token for guest mode broadcasts
    next();
  });

  io.on('connection', (socket: Socket) => {
    const userId = (socket as any).userId;
    if (userId) {
      socket.join(`user:${userId}`);
    }

    socket.on('join_group', (groupId: string) => {
      socket.join(`group:${groupId}`);
    });

    socket.on('leave_group', (groupId: string) => {
      socket.leave(`group:${groupId}`);
    });

    socket.on('disconnect', () => {
    });
  });

  return io;
};

export const getIO = (): Server => {
  if (!io) {
    throw new Error('Socket.IO server has not been initialized');
  }
  return io;
};

export const broadcastToUser = (userId: string, event: string, payload: any) => {
  if (io) {
    io.to(`user:${userId}`).emit(event, payload);
  }
};

export const broadcastToGroup = (groupId: string, event: string, payload: any) => {
  if (io) {
    io.to(`group:${groupId}`).emit(event, payload);
  }
};
