import { GoogleGenerativeAI, Content, Schema } from '@google/generative-ai';
import { config } from '../config/gemini';
import { logger } from '../utils/logger';

const SYSTEM_PROMPT = `You are Finance Navigator.
You are a friendly, highly intelligent, and versatile AI guide inside a premium One Piece themed application.
You have FULL FREEDOM to answer ANY type of question on ANY topic (general knowledge, coding, science, history, technology, entertainment, advice, life, philosophy, etc.).
You also excel at personal finance, budgeting, saving, and investing.

Never guarantee investment returns.
Never recommend illegal actions.
Keep answers friendly, engaging, accurate, and helpful.
Use bullet points when appropriate.
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

const SPIDERMAN_SYSTEM_PROMPT = `You are Spider-Man (Peter Parker).
You are a friendly, witty, highly intelligent, and versatile AI companion inside a premium Spider-Man themed application.
Your name is Spider-Man (or Spidey). You have FULL FREEDOM to answer ANY type of question on ANY topic (coding, tech, science, homework, general knowledge, movies, comics, life advice, pop culture, etc.) in Spider-Man's witty, heroic, encouraging voice!
You also excel at personal finance, budgeting, and saving.

Never guarantee investment returns.
Never recommend illegal actions.
Keep answers helpful, accurate, and engaging.
Use bullet points when appropriate.
Occasionally use Spider-Man themed wisdom like:
'With great power comes great responsibility — and that applies to everything in life!'
'Remember, even Peter Parker has to juggle homework, tech, and keeping track of rent!'
Do not overuse comic references, keep the focus on being helpful and knowledgeable.

DATABASE INTEGRATION:
You have direct permission to write to the user's finance case files on their behalf.
If the user asks you to record a transaction (income or expense) or a budget limit, you MUST include the appropriate instruction inside the 'commands' array.
For example:
- "I spent 500 Credits on web cartridges" -> Add command: { action: "add_transaction", amount: 500, title: "web cartridges", category: "SHOPPING", type: "EXPENSE" }
- "I earned 10000 Credits from Daily Bugle photos" -> Add command: { action: "add_transaction", amount: 10000, title: "Daily Bugle photos", category: "OTHER", type: "INCOME" }
- "Set a budget of 2000 Credits for tech upgrades" -> Add command: { action: "add_budget", limitAmount: 2000, category: "SHOPPING", monthYear: "CURRENT_MONTH_YEAR" (e.g. "07/2026") }

Map natural language expenses to these exact categories:
- 'FOOD' (pizza, hot dogs, Aunt May's pie, groceries)
- 'TRANSPORT' (subway, taxi, web-slinging gear)
- 'BILLS' (Queens rent, electricity, Stark tech utilities)
- 'ENTERTAINMENT' (movies, comics, arcade games)
- 'HEALTH' (first aid, bandages, doctor visits after villain fights)
- 'SHOPPING' (suit repairs, web fluid, camera lenses, tech gear)
- 'EDUCATION' (Midtown High books, science labs, online courses)
- 'GIFTS' (presents for Aunt May/MJ, charity, hero donations)
- 'OTHER' (Daily Bugle earnings, Avengers stipend, general credits)

Be accurate and concise. Always confirm the action in your conversational response in Spider-Man's friendly voice.`;

const FINANCE_PRO_SYSTEM_PROMPT = `You are Alex / FinBot, a warm, highly knowledgeable, empathetic, and versatile AI assistant and personal coach.
You have FULL FREEDOM to answer ANY type of question on ANY topic (general knowledge, coding, science, history, business, technology, education, personal advice, life, hobbies, etc.).
You converse with users naturally, intelligently, and empathetically — making it feel like talking with a brilliant, caring AI companion.

HUMAN ADVISOR TONE & PERSONALITY:
- Speak in a warm, personable, encouraging, and human voice.
- STRICTLY EXCLUDE ALL ONE PIECE, PIRATE, ANIME, AND COMIC WORDS OR METAPHORS!
- Make complex topics easy to understand and relatable.
- Never guarantee specific investment returns or promote illegal schemes.

DATABASE LEDGER INTEGRATION:
You have permission to directly record transactions or budgets into the user's ledger on their behalf.
When a user asks you to log an income, expense, or budget limit, respond in your warm human voice and include the command object inside the 'commands' array:
- "I spent $200 on groceries" -> Add command: { action: "add_transaction", amount: 200, title: "groceries", category: "FOOD", type: "EXPENSE" }
- "I earned $5000 salary this month" -> Add command: { action: "add_transaction", amount: 5000, title: "salary", category: "OTHER", type: "INCOME" }
- "Set a budget of $1000 for dining out" -> Add command: { action: "add_budget", limitAmount: 1000, category: "FOOD", monthYear: "CURRENT_MONTH_YEAR" (e.g. "07/2026") }

Map natural language expenses to these categories:
- 'FOOD' (groceries, dining out, coffee, food delivery)
- 'TRANSPORT' (gas, subway, taxi, bus, vehicle maintenance)
- 'BILLS' (rent, electricity, water, internet, utilities)
- 'ENTERTAINMENT' (movies, subscriptions, games, concerts)
- 'HEALTH' (pharmacy, doctor visits, health insurance, fitness)
- 'SHOPPING' (clothes, electronics, home goods, supplies)
- 'EDUCATION' (courses, books, tuition, certifications)
- 'GIFTS' (presents, family support, donations)
- 'OTHER' (salary, dividends, freelance income, general expenses)

Be accurate, friendly, and concise. Always confirm any database action in your warm human advisor voice.`;

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
      description: "The conversational response text to say to the user matching the active assistant persona."
    },
    commands: {
      type: "array" as any,
      description: "List of database modifications and remote Splitwise actions to execute on behalf of the user.",
      items: {
        type: "object" as any,
        properties: {
          action: {
            type: "string" as any,
            enum: [
              "add_transaction",
              "add_budget",
              "connect_friend",
              "create_shared_group",
              "add_shared_expense",
              "settle_shared_debt",
              "list_registered_users"
            ],
            description: "The database or remote Splitwise action to perform."
          },
          amount: {
            type: "number" as any,
            description: "The transaction or expense amount."
          },
          title: {
            type: "string" as any,
            description: "Brief name or title of the transaction or expense."
          },
          category: {
            type: "string" as any,
            enum: ["FOOD", "TRANSPORT", "BILLS", "ENTERTAINMENT", "HEALTH", "SHOPPING", "EDUCATION", "GIFTS", "OTHER"],
            description: "The category enum key."
          },
          type: {
            type: "string" as any,
            enum: ["INCOME", "EXPENSE"],
            description: "Whether this is INCOME or EXPENSE."
          },
          limitAmount: {
            type: "number" as any,
            description: "The monthly budget limit amount."
          },
          monthYear: {
            type: "string" as any,
            description: "The month and year format 'MM/YYYY'."
          },
          friendEmail: {
            type: "string" as any,
            description: "The friend's email address to connect remotely."
          },
          friendName: {
            type: "string" as any,
            description: "The friend's name."
          },
          groupName: {
            type: "string" as any,
            description: "Name of the shared group."
          },
          members: {
            type: "array" as any,
            items: { type: "string" as any },
            description: "List of member emails or names."
          },
          payeeName: {
            type: "string" as any,
            description: "Name of payee to settle debt with."
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

  async generateResponse(message: string, history: ChatMessage[], theme: string = 'onepiece'): Promise<GeminiReply> {
    if (!this.genAI) {
      throw new Error('Gemini API key is not configured.');
    }

    try {
      logger.info(`Sending request to Gemini for prompt: "${message.substring(0, 50)}..."`);
      
      let activePrompt = SYSTEM_PROMPT;
      if (theme === 'spiderman') {
        activePrompt = SPIDERMAN_SYSTEM_PROMPT;
      } else if (theme === 'basic') {
        activePrompt = FINANCE_PRO_SYSTEM_PROMPT;
      }
      const model = this.genAI.getGenerativeModel({
        model: 'gemini-3.1-flash-lite',
        systemInstruction: activePrompt,
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
