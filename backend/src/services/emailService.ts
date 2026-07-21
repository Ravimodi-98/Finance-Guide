import { Resend } from 'resend';

const resendApiKey = process.env.RESEND_API_KEY;
const resend = new Resend(resendApiKey);

export const sendVerificationEmail = async (toEmail: string, code: string): Promise<boolean> => {
  try {
    const fromAddress = process.env.EMAIL_FROM || 'onboarding@resend.dev';

    const response = await resend.emails.send({
      from: fromAddress,
      to: [toEmail],
      subject: 'Verify your Email',
      html: `
        <div style="font-family: Arial, sans-serif; padding: 20px; background-color: #f4f6f8; color: #333;">
          <div style="max-width: 500px; margin: 0 auto; background: #ffffff; padding: 30px; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">
            <h2 style="color: #4F46E5; margin-top: 0;">Verify your Email</h2>
            <p>Hello,</p>
            <p>Your verification code is:</p>
            <div style="background-color: #EEF2FF; border: 2px dashed #6366F1; font-size: 32px; font-weight: bold; letter-spacing: 6px; text-align: center; padding: 15px; border-radius: 8px; margin: 20px 0; color: #312E81;">
              ${code}
            </div>
            <p style="color: #6B7280; font-size: 14px;">This code expires in 10 minutes.</p>
            <p style="color: #9CA3AF; font-size: 12px; margin-top: 30px;">If you didn't create this account, please ignore this email.</p>
          </div>
        </div>
      `,
      text: `Hello,\n\nYour verification code is:\n\n${code}\n\nThis code expires in 10 minutes.\n\nIf you didn't create this account, please ignore this email.`
    });

    if (response.error) {
      console.error('Resend Email Error:', response.error);
      return false;
    }

    return true;
  } catch (error) {
    console.error('Failed to send verification email via Resend:', error);
    return false;
  }
};
