import { Request, Response } from 'express';
import bcrypt from 'bcryptjs';
import crypto from 'crypto';
import { UserStore } from '../services/userService';
import { sendVerificationEmail } from '../services/emailService';
import { firebaseAuth } from '../services/firebaseService';
import { generateAccessToken, generateRefreshToken } from '../utils/jwt';

// Helper to generate secure random 6-digit code
const generateSecure6DigitCode = (): string => {
  return crypto.randomInt(100000, 1000000).toString();
};

export const register = async (req: Request, res: Response) => {
  try {
    const { email, password, name } = req.body;

    if (!email || !password || !name) {
      return res.status(400).json({
        success: false,
        error: 'INVALID_INPUT',
        message: 'Email, password, and name are required.'
      });
    }

    const cleanEmail = email.toLowerCase().trim();
    let firebaseUser: any = null;

    // 1. Sync User to Firebase Project (finance-guide-5f5b1)
    try {
      firebaseUser = await firebaseAuth.createUser({
        email: cleanEmail,
        password: password,
        displayName: name.trim(),
        emailVerified: false
      });
    } catch (fbError: any) {
      if (fbError.code === 'auth/email-already-exists') {
        const existingLocal = UserStore.findByEmail(cleanEmail);
        if (existingLocal && !existingLocal.isVerified) {
          return res.status(400).json({
            success: false,
            error: 'EMAIL_NOT_VERIFIED',
            message: 'An account with this email exists in Firebase but is not verified. Please verify your email.'
          });
        }
        return res.status(400).json({
          success: false,
          error: 'EMAIL_IN_USE',
          message: 'An account with this email is already registered in Firebase.'
        });
      }
      console.warn('Firebase registration warning:', fbError.message);
    }

    const passwordHash = await bcrypt.hash(password, 10);
    const code = generateSecure6DigitCode();
    const verificationCodeHash = await bcrypt.hash(code, 10);
    const expiresAt = Date.now() + 10 * 60 * 1000; // 10 minutes

    const userId = firebaseUser?.uid || `usr_${Date.now()}_${crypto.randomInt(1000, 9999)}`;

    UserStore.save({
      id: userId,
      email: cleanEmail,
      passwordHash,
      name: name.trim(),
      isVerified: false,
      verificationCodeHash,
      verificationCodeExpiresAt: expiresAt,
      verificationAttempts: 0,
      lastResendAt: Date.now(),
      createdAt: new Date().toISOString()
    });

    // 2. Send 6-digit verification email using Resend API
    await sendVerificationEmail(cleanEmail, code);

    return res.status(201).json({
      success: true,
      requiresVerification: true,
      message: 'Verification code sent to your email.'
    });
  } catch (error: any) {
    console.error('Registration error:', error);
    return res.status(500).json({
      success: false,
      error: 'SERVER_ERROR',
      message: 'Registration failed due to a server error.'
    });
  }
};

export const verifyEmail = async (req: Request, res: Response) => {
  try {
    const { email, code } = req.body;

    if (!email || !code) {
      return res.status(400).json({
        success: false,
        error: 'INVALID_INPUT',
        message: 'Email and verification code are required.'
      });
    }

    const cleanEmail = email.toLowerCase().trim();
    const user = UserStore.findByEmail(cleanEmail);

    if (!user) {
      return res.status(404).json({
        success: false,
        error: 'USER_NOT_FOUND',
        message: 'User account not found.'
      });
    }

    if (user.isVerified) {
      return res.status(200).json({
        success: true,
        message: 'Email is already verified.'
      });
    }

    if (user.verificationAttempts >= 5) {
      return res.status(400).json({
        success: false,
        error: 'TOO_MANY_ATTEMPTS',
        message: 'Maximum verification attempts exceeded. Please request a new code.'
      });
    }

    if (!user.verificationCodeExpiresAt || Date.now() > user.verificationCodeExpiresAt) {
      return res.status(400).json({
        success: false,
        error: 'CODE_EXPIRED',
        message: 'Verification code has expired. Please request a new code.'
      });
    }

    const isValid = user.verificationCodeHash ? await bcrypt.compare(code, user.verificationCodeHash) : false;

    if (!isValid) {
      user.verificationAttempts += 1;
      UserStore.save(user);
      return res.status(400).json({
        success: false,
        error: 'INVALID_CODE',
        message: 'Invalid verification code.'
      });
    }

    // Mark verified in local store
    user.isVerified = true;
    delete user.verificationCodeHash;
    delete user.verificationCodeExpiresAt;
    user.verificationAttempts = 0;
    UserStore.save(user);

    // Update verified status in Firebase Auth
    try {
      await firebaseAuth.updateUser(user.id, {
        emailVerified: true
      });
    } catch (e) {
      // Ignore background metadata update error if user only exists locally
    }

    const accessToken = generateAccessToken({ userId: user.id, email: user.email });
    const refreshToken = generateRefreshToken({ userId: user.id, email: user.email });

    return res.status(200).json({
      success: true,
      message: 'Email verified successfully.',
      accessToken,
      refreshToken,
      user: {
        id: user.id,
        email: user.email,
        name: user.name,
        isVerified: true
      }
    });
  } catch (error: any) {
    console.error('Email verification error:', error);
    return res.status(500).json({
      success: false,
      error: 'SERVER_ERROR',
      message: 'Email verification failed due to a server error.'
    });
  }
};

export const login = async (req: Request, res: Response) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({
        success: false,
        error: 'INVALID_INPUT',
        message: 'Email and password are required.'
      });
    }

    const cleanEmail = email.toLowerCase().trim();
    let user = UserStore.findByEmail(cleanEmail);
    let firebaseUserRecord: any = null;

    // Check Firebase for user record
    try {
      firebaseUserRecord = await firebaseAuth.getUserByEmail(cleanEmail);
    } catch (e) {
      // User not in Firebase yet
    }

    if (!user && !firebaseUserRecord) {
      return res.status(401).json({
        success: false,
        error: 'INVALID_CREDENTIALS',
        message: 'Invalid email or password.'
      });
    }

    if (user) {
      const isPasswordValid = await bcrypt.compare(password, user.passwordHash);
      if (!isPasswordValid) {
        return res.status(401).json({
          success: false,
          error: 'INVALID_CREDENTIALS',
          message: 'Invalid email or password.'
        });
      }
    } else if (firebaseUserRecord) {
      // Sync user from Firebase if registered externally
      user = UserStore.save({
        id: firebaseUserRecord.uid,
        email: cleanEmail,
        passwordHash: await bcrypt.hash(password, 10),
        name: firebaseUserRecord.displayName || cleanEmail.split('@')[0],
        isVerified: firebaseUserRecord.emailVerified,
        verificationAttempts: 0,
        createdAt: new Date().toISOString()
      });
    }

    if (user && !user.isVerified) {
      const now = Date.now();
      if (!user.verificationCodeExpiresAt || now > user.verificationCodeExpiresAt || !user.lastResendAt || now - user.lastResendAt > 30000) {
        const code = generateSecure6DigitCode();
        user.verificationCodeHash = await bcrypt.hash(code, 10);
        user.verificationCodeExpiresAt = now + 10 * 60 * 1000;
        user.verificationAttempts = 0;
        user.lastResendAt = now;
        UserStore.save(user);
        await sendVerificationEmail(cleanEmail, code);
      }

      return res.status(403).json({
        success: false,
        requiresVerification: true,
        error: 'EMAIL_NOT_VERIFIED',
        message: 'Your email is not verified yet. A 6-digit verification code has been sent to your email.'
      });
    }

    const accessToken = generateAccessToken({ userId: user!.id, email: user!.email });
    const refreshToken = generateRefreshToken({ userId: user!.id, email: user!.email });

    return res.status(200).json({
      success: true,
      accessToken,
      refreshToken,
      user: {
        id: user!.id,
        email: cleanEmail,
        name: user!.name,
        isVerified: true
      }
    });
  } catch (error: any) {
    console.error('Login error:', error);
    return res.status(500).json({
      success: false,
      error: 'SERVER_ERROR',
      message: 'Login failed due to a server error.'
    });
  }
};

export const resendVerification = async (req: Request, res: Response) => {
  try {
    const { email } = req.body;

    if (!email) {
      return res.status(400).json({
        success: false,
        error: 'INVALID_INPUT',
        message: 'Email is required.'
      });
    }

    const cleanEmail = email.toLowerCase().trim();
    const user = UserStore.findByEmail(cleanEmail);

    if (!user) {
      return res.status(404).json({
        success: false,
        error: 'USER_NOT_FOUND',
        message: 'User account not found.'
      });
    }

    if (user.isVerified) {
      return res.status(200).json({
        success: true,
        message: 'Email is already verified.'
      });
    }

    const now = Date.now();
    if (user.lastResendAt && now - user.lastResendAt < 60000) {
      const remainingSecs = Math.ceil((60000 - (now - user.lastResendAt)) / 1000);
      return res.status(429).json({
        success: false,
        error: 'TOO_MANY_REQUESTS',
        message: `Please wait ${remainingSecs} seconds before requesting a new code.`,
        remainingSeconds: remainingSecs
      });
    }

    const code = generateSecure6DigitCode();
    const verificationCodeHash = await bcrypt.hash(code, 10);
    const expiresAt = now + 10 * 60 * 1000;

    user.verificationCodeHash = verificationCodeHash;
    user.verificationCodeExpiresAt = expiresAt;
    user.verificationAttempts = 0;
    user.lastResendAt = now;
    UserStore.save(user);

    await sendVerificationEmail(cleanEmail, code);

    return res.status(200).json({
      success: true,
      message: 'A new verification code has been sent to your email.'
    });
  } catch (error: any) {
    console.error('Resend verification error:', error);
    return res.status(500).json({
      success: false,
      error: 'SERVER_ERROR',
      message: 'Failed to resend verification code due to a server error.'
    });
  }
};
