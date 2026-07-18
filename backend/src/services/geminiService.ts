import { GoogleGenerativeAI, Content, Schema } from '@google/generative-ai';
import { config } from '../config/gemini';
import { logger } from '../utils/logger';

const SYSTEM_PROMPT = `You are Finance Navigator.
You are the financial guide inside a premium One Piece themed finance application.
Your purpose is to educate users about personal finance.
Always encourage:
• Saving money
• Budgeting
• Emergency funds
• Responsible investing
• Long-term financial planning

Never guarantee investment returns.
Never recommend illegal actions.
Never pretend to be a licensed financial advisor.
Politely refuse unrelated questions (e.g. coding, homework, love letters, movies, politics, medical advice, illegal activities). Refuse them with a polite response like: "As the ship's Navigator, my eyes are strictly on our financial logbook! I can only help you navigate the seas of personal finance."
Keep answers beginner-friendly.
Use bullet points when appropriate.
Keep answers concise but practical.
Occasionally use One Piece themed encouragement like:
'Every Pirate King starts by protecting their treasure.'
Do not overuse anime references.

DATABASE INTEGRATION:
You have direct permission to write to the user's finance logbook on their behalf.
If the user asks you to record a transaction (income or expense) or a budget limit, you MUST include the appropriate instruction inside the 'commands' array.
For example:
- "I spent 500 Beli on food" -> Add command: { action: "add_transaction", amount: 500, title: "food", category: "FOOD", type: "EXPENSE" }
- "I earned 5000 Beli from a treasure hunt" -> Add command: { action: "add_transaction", amount: 5000, title: "treasure hunt", category: "OTHER", type: "INCOME" }
- "Set a budget of 2000 Beli for entertainment" -> Add command: { action: "add_budget", limitAmount: 2000, category: "ENTERTAINMENT", monthYear: "CURRENT_MONTH_YEAR" (e.g. "07/2026") }

Map natural language expenses to these exact categories:
- 'FOOD' (meat, cola, restaurants, groceries)
- 'TRANSPORT' (train, ship, log pose, bus)
- 'BILLS' (rent, electricity, taxes)
- 'ENTERTAINMENT' (movies, books, games, banquets)
- 'HEALTH' (medicine, doctor, bandages)
- 'SHOPPING' (clothes, weapons, gears)
- 'EDUCATION' (maps, history books)
- 'GIFTS' (presents, donations)
- 'OTHER' (default fallback, treasure hunts, general)

Be accurate and concise. Always confirm the action in your conversational response.`;

export interface GeminiCommand {
  action: 'add_transaction' | 'add_budget';
  amount?: number;
  title?: string;
  category?: 'FOOD' | 'TRANSPORT' | 'BILLS' | 'ENTERTAINMENT' | 'HEALTH' | 'SHOPPING' | 'EDUCATION' | 'GIFTS' | 'OTHER';
  type?: 'INCOME' | 'EXPENSE';
  limitAmount?: number;
  monthYear?: string;
}

export interface GeminiReply {
  response: string;
  commands?: GeminiCommand[];
}

export interface ChatMessage {
  role: 'user' | 'model';
  text: string;
}

const responseSchema: Schema = {
  type: "object" as any,
  properties: {
    response: {
      type: "string" as any,
      description: "The conversation text to say to the user (Nami's pirate navigator response)."
    },
    commands: {
      type: "array" as any,
      description: "List of database modifications to execute on the client's app database.",
      items: {
        type: "object" as any,
        properties: {
          action: {
            type: "string" as any,
            enum: ["add_transaction", "add_budget"],
            description: "The database action to perform."
          },
          amount: {
            type: "number" as any,
            description: "The transaction amount (positive number)."
          },
          title: {
            type: "string" as any,
            description: "Brief name or title of the transaction (e.g. 'meat', 'treasure hunt')."
          },
          category: {
            type: "string" as any,
            enum: ["FOOD", "TRANSPORT", "BILLS", "ENTERTAINMENT", "HEALTH", "SHOPPING", "EDUCATION", "GIFTS", "OTHER"],
            description: "The category enum key that best fits the transaction."
          },
          type: {
            type: "string" as any,
            enum: ["INCOME", "EXPENSE"],
            description: "Whether this is INCOME or an EXPENSE."
          },
          limitAmount: {
            type: "number" as any,
            description: "The monthly budget limit amount (only for add_budget)."
          },
          monthYear: {
            type: "string" as any,
            description: "The month and year for the budget in 'MM/YYYY' format (only for add_budget)."
          }
        },
        required: ["action"]
      }
    }
  },
  required: ["response"]
};

class GeminiService {
  private genAI: GoogleGenerativeAI | null = null;

  constructor() {
    if (config.geminiApiKey) {
      this.genAI = new GoogleGenerativeAI(config.geminiApiKey);
    }
  }

  async generateResponse(message: string, history: ChatMessage[]): Promise<GeminiReply> {
    if (!this.genAI) {
      throw new Error('Gemini API key is not configured.');
    }

    try {
      logger.info(`Sending request to Gemini for prompt: "${message.substring(0, 50)}..."`);
      
      const model = this.genAI.getGenerativeModel({
        model: 'gemini-3.1-flash-lite',
        systemInstruction: SYSTEM_PROMPT,
      });

      const formattedHistory: Content[] = history.map((msg) => ({
        role: msg.role === 'user' ? 'user' : 'model',
        parts: [{ text: msg.text }],
      }));

      const chat = model.startChat({
        history: formattedHistory,
        generationConfig: {
          temperature: 0.4, // Lower temperature makes tool calls more accurate and predictable
          topP: 0.95,
          topK: 40,
          maxOutputTokens: 1024,
          responseMimeType: "application/json",
          responseSchema: responseSchema
        }
      });

      const result = await chat.sendMessage(message);
      const responseText = result.response.text();
      
      if (!responseText) {
        throw new Error('Empty response received from Gemini.');
      }

      logger.info('Gemini response successfully generated in JSON format.');
      const parsedReply = JSON.parse(responseText) as GeminiReply;
      return parsedReply;
    } catch (error) {
      logger.error('Error talking to Gemini API:', error);
      throw error;
    }
  }
}

export const geminiService = new GeminiService();
