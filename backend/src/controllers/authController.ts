import { Request, Response } from 'express';
import bcrypt from 'bcryptjs';
import crypto from 'crypto';
import { UserStore } from '../services/userService';
import { sendVerificationEmail } from '../services/emailService';
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
    const existing = UserStore.findByEmail(cleanEmail);

    if (existing) {
      if (!existing.isVerified) {
        // If account exists but is not verified, trigger a resend and prompt for verification
        return res.status(400).json({
          success: false,
          error: 'EMAIL_NOT_VERIFIED',
          message: 'An account with this email exists but is not verified. Please verify your email.'
        });
      }
      return res.status(400).json({
        success: false,
        error: 'EMAIL_IN_USE',
        message: 'An account with this email already exists.'
      });
    }

    const passwordHash = await bcrypt.hash(password, 10);
    const code = generateSecure6DigitCode();
    const verificationCodeHash = await bcrypt.hash(code, 10);
    const expiresAt = Date.now() + 10 * 60 * 1000; // 10 minutes

    const newUser = UserStore.save({
      id: `usr_${Date.now()}_${crypto.randomInt(1000, 9999)}`,
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

    // Send email using Resend
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
      return res.status(444).json({
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

    // Check attempt limit
    if (user.verificationAttempts >= 5) {
      return res.status(400).json({
        success: false,
        error: 'TOO_MANY_ATTEMPTS',
        message: 'Maximum verification attempts exceeded. Please request a new code.'
      });
    }

    // Check code expiration
    if (!user.verificationCodeExpiresAt || Date.now() > user.verificationCodeExpiresAt) {
      return res.status(400).json({
        success: false,
        error: 'CODE_EXPIRED',
        message: 'Verification code has expired. Please request a new code.'
      });
    }

    // Verify code match
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

    // Success: mark as verified and clear code fields
    user.isVerified = true;
    delete user.verificationCodeHash;
    delete user.verificationCodeExpiresAt;
    user.verificationAttempts = 0;
    UserStore.save(user);

    // Generate JWT tokens
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
    const user = UserStore.findByEmail(cleanEmail);

    if (!user) {
      return res.status(401).json({
        success: false,
        error: 'INVALID_CREDENTIALS',
        message: 'Invalid email or password.'
      });
    }

    // Check password first
    const isPasswordValid = await bcrypt.compare(password, user.passwordHash);
    if (!isPasswordValid) {
      return res.status(401).json({
        success: false,
        error: 'INVALID_CREDENTIALS',
        message: 'Invalid email or password.'
      });
    }

    // Then check if email is verified
    if (!user.isVerified) {
      return res.status(403).json({
        success: false,
        error: 'EMAIL_NOT_VERIFIED',
        message: 'Please verify your email first.'
      });
    }

    // Only verified users receive JWT tokens
    const accessToken = generateAccessToken({ userId: user.id, email: user.email });
    const refreshToken = generateRefreshToken({ userId: user.id, email: user.email });

    return res.status(200).json({
      success: true,
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

    // Enforce 60-second rate limit
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

    // Generate new code & extend expiry by 10 min
    const code = generateSecure6DigitCode();
    const verificationCodeHash = await bcrypt.hash(code, 10);
    const expiresAt = now + 10 * 60 * 1000;

    user.verificationCodeHash = verificationCodeHash;
    user.verificationCodeExpiresAt = expiresAt;
    user.verificationAttempts = 0;
    user.lastResendAt = now;
    UserStore.save(user);

    // Send email using Resend
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
