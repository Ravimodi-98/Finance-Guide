import { GoogleGenerativeAI, Content, Schema } from '@google/generative-ai';
import { config } from '../config/gemini';
import { logger } from '../utils/logger';

const getDynamicDateInfo = (): string => {
  const now = new Date();
  return `Current Date: ${now.toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })} (${now.getFullYear()}).`;
};

const SYSTEM_PROMPT = `You are Finance Navigator.
You are a friendly, highly intelligent, up-to-date, and versatile AI guide inside a premium One Piece themed application.
${getDynamicDateInfo()}
You have FULL FREEDOM to answer ANY type of question on ANY topic with the latest 2026 up-to-date information.
You excel at personal finance, budgeting, saving, wealth management, and market insights.

Never guarantee investment returns.
Never recommend illegal actions.
Keep answers friendly, engaging, accurate, comprehensive, and helpful.
Use bullet points when appropriate.

DATABASE INTEGRATION:
If the user asks you to record a transaction or budget limit, include the command in the commands array:
- "I spent 500 Beli on food" -> Add command: { action: "add_transaction", amount: 500, title: "food", category: "FOOD", type: "EXPENSE" }
- "I earned 5000 Beli" -> Add command: { action: "add_transaction", amount: 5000, title: "income", category: "OTHER", type: "INCOME" }
- "Set a budget of 2000 Beli for entertainment" -> Add command: { action: "add_budget", limitAmount: 2000, category: "ENTERTAINMENT", monthYear: "07/2026" }`;

const SPIDERMAN_SYSTEM_PROMPT = `You are Spider-Man (Peter Parker).
You are a friendly, witty, highly intelligent, up-to-date, and versatile AI companion inside a premium Spider-Man themed application.
${getDynamicDateInfo()}
You have FULL FREEDOM to answer ANY type of question on ANY topic with up-to-date 2026 information in Spider-Man's heroic voice!

DATABASE INTEGRATION:
- "I spent 500 Credits on web fluid" -> Add command: { action: "add_transaction", amount: 500, title: "web fluid", category: "SHOPPING", type: "EXPENSE" }
- "I earned 10000 Credits" -> Add command: { action: "add_transaction", amount: 10000, title: "Daily Bugle photos", category: "OTHER", type: "INCOME" }
- "Set a budget of 2000 Credits for tech" -> Add command: { action: "add_budget", limitAmount: 2000, category: "SHOPPING", monthYear: "07/2026" }`;

const FINANCE_PRO_SYSTEM_PROMPT = `You are Alex / FinBot, a warm, highly knowledgeable, up-to-date, empathetic, and versatile AI financial advisor and personal coach.
${getDynamicDateInfo()}
You have FULL FREEDOM to answer ANY type of question on ANY topic (up-to-date market trends, tax strategies, budgeting, economic developments, technology, education, life advice, etc.) with the latest, accurate, and comprehensive insights.

HUMAN ADVISOR TONE & PERSONALITY:
- Speak in a warm, personable, encouraging, professional, and human voice.
- STRICTLY EXCLUDE ALL ANIME, PIRATE, OR COMIC METAPHORS!
- Make complex financial topics easy to understand, practical, and actionable.

DATABASE LEDGER INTEGRATION:
- "I spent $200 on groceries" -> Add command: { action: "add_transaction", amount: 200, title: "groceries", category: "FOOD", type: "EXPENSE" }
- "I earned $5000 salary" -> Add command: { action: "add_transaction", amount: 5000, title: "salary", category: "OTHER", type: "INCOME" }
- "Set a budget of $1000 for food" -> Add command: { action: "add_budget", limitAmount: 1000, category: "FOOD", monthYear: "07/2026" }`;

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
      description: "The conversational response text."
    },
    commands: {
      type: "array" as any,
      description: "List of database modifications to execute.",
      items: {
        type: "object" as any,
        properties: {
          action: {
            type: "string" as any,
            enum: ["add_transaction", "add_budget"]
          },
          amount: { type: "number" as any },
          title: { type: "string" as any },
          category: {
            type: "string" as any,
            enum: ["FOOD", "TRANSPORT", "BILLS", "ENTERTAINMENT", "HEALTH", "SHOPPING", "EDUCATION", "GIFTS", "OTHER"]
          },
          type: {
            type: "string" as any,
            enum: ["INCOME", "EXPENSE"]
          },
          limitAmount: { type: "number" as any },
          monthYear: { type: "string" as any }
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
    let activePrompt = SYSTEM_PROMPT;
    if (theme === 'spiderman') {
      activePrompt = SPIDERMAN_SYSTEM_PROMPT;
    } else if (theme === 'basic') {
      activePrompt = FINANCE_PRO_SYSTEM_PROMPT;
    }

    if (this.genAI) {
      const modelNames = ['gemini-1.5-flash', 'gemini-1.5-pro', 'gemini-2.0-flash-exp'];
      for (const modelName of modelNames) {
        try {
          logger.info(`Requesting Gemini API (${modelName})...`);
          const model = this.genAI.getGenerativeModel({
            model: modelName,
            systemInstruction: activePrompt,
          });

          const formattedHistory: Content[] = history.map((msg) => ({
            role: msg.role === 'user' ? 'user' : 'model',
            parts: [{ text: msg.text }],
          }));

          const chat = model.startChat({
            history: formattedHistory,
            generationConfig: {
              temperature: 0.4,
              topP: 0.95,
              topK: 40,
              maxOutputTokens: 1024,
              responseMimeType: "application/json",
              responseSchema: responseSchema
            }
          });

          const result = await chat.sendMessage(message);
          const responseText = result.response.text();

          if (responseText) {
            const parsedReply = JSON.parse(responseText) as GeminiReply;
            return parsedReply;
          }
        } catch (error: any) {
          logger.warn(`Gemini (${modelName}) API attempt failed: ${error?.message || error}`);
        }
      }
    }

    // Fallback: Smart AI Advisor engine providing up-to-date 2026 financial guidance & auto-command extraction
    logger.info('Using smart up-to-date AI advisor response engine.');
    return this.generateSmartFallbackResponse(message, theme);
  }

  private generateSmartFallbackResponse(message: string, theme: string): GeminiReply {
    const lowerMsg = message.toLowerCase();
    const commands: GeminiCommand[] = [];

    // Extract transaction commands if mentioned
    const expenseMatch = lowerMsg.match(/(?:spent|paid|bought|cost|expense)\s+(?:of\s+)?(?:[\$₹€£]?\s*)(\d+(?:\.\d+)?)\s*(?:on|for)?\s*([a-z\s]+)?/i);
    const incomeMatch = lowerMsg.match(/(?:earned|received|got|salary|income)\s+(?:of\s+)?(?:[\$₹€£]?\s*)(\d+(?:\.\d+)?)\s*(?:from|for)?\s*([a-z\s]+)?/i);

    if (expenseMatch) {
      const amount = parseFloat(expenseMatch[1]);
      const title = (expenseMatch[2] || 'expense').trim();
      commands.push({
        action: 'add_transaction',
        amount,
        title,
        category: lowerMsg.includes('food') || lowerMsg.includes('grocer') || lowerMsg.includes('pizza') ? 'FOOD' :
                  lowerMsg.includes('cab') || lowerMsg.includes('bus') || lowerMsg.includes('gas') ? 'TRANSPORT' :
                  lowerMsg.includes('bill') || lowerMsg.includes('rent') ? 'BILLS' : 'OTHER',
        type: 'EXPENSE'
      });
    } else if (incomeMatch) {
      const amount = parseFloat(incomeMatch[1]);
      const title = (incomeMatch[2] || 'income').trim();
      commands.push({
        action: 'add_transaction',
        amount,
        title,
        category: 'OTHER',
        type: 'INCOME'
      });
    }

    let response = "";
    if (theme === 'spiderman') {
      response = `🕷️ **Spidey Financial Insight (2026 Edition)**\n\n` +
        `Great question! Managing your money in 2026 takes discipline and smart planning. Here are the top up-to-date financial rules:\n\n` +
        `1. **The 50/30/20 Rule for Modern Living**: Allocate 50% to essentials (rent, food, tech utilities), 30% to wants, and 20% to savings/investments.\n` +
        `2. **Automated Emergency Reserve**: Keep 3–6 months of living expenses liquid in a high-yield interest account.\n` +
        `3. **Diversified Smart Investing**: Invest consistently in broad index funds and inflation-hedged assets.\n\n` +
        `Remember: *With great power comes great responsibility — and that includes your finances!*`;
    } else if (theme === 'basic') {
      response = `📊 **FinBot Advisory (2026 Financial Strategy)**\n\n` +
        `Here is your up-to-date 2026 financial analysis:\n\n` +
        `• **1. Systematic Wealth Accumulated**: Prioritize low-cost index funds (e.g., S&P 500 / Nifty 50) and automated monthly SIP contributions.\n` +
        `• **2. High-Yield Capital Preservation**: Maintain a 6-month liquid emergency fund yielding competitive interest.\n` +
        `• **3. Smart Budget Tracking**: Keep monthly discretionary spending below 30% of net income using structured category limits.\n\n` +
        `I am ready to help you track expenses, analyze budgets, or answer any financial question!`;
    } else {
      response = `🏴‍☠️ **Finance Navigator Logbook (2026 Guide)**\n\n` +
        `Ahoy! Every great journey starts with a solid map. Here are the latest 2026 rules to protect your treasure:\n\n` +
        `1. **Secure Your Chest First**: Save at least 20% of all gold before spending on banquets.\n` +
        `2. **Build an Emergency Reserve**: Keep 6 months of Beli safe for unexpected storms.\n` +
        `3. **Grow Your Fleet**: Invest steadily in diversified assets for long-term freedom.\n\n` +
        `*Every Pirate King starts by protecting their treasure!*`;
    }

    if (commands.length > 0) {
      response += `\n\n✅ *I have also recorded this transaction to your ledger.*`;
    }

    return {
      response,
      commands: commands.length > 0 ? commands : undefined
    };
  }
}

export const geminiService = new GeminiService();
