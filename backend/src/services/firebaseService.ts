import * as admin from 'firebase-admin';

// Initialize Firebase Admin SDK for project finance-guide-5f5b1
if (!admin.apps.length) {
  admin.initializeApp({
    projectId: process.env.FIREBASE_PROJECT_ID || 'finance-guide-5f5b1'
  });
}

export const firebaseAuth = admin.auth();
export const firebaseDb = admin.firestore();
