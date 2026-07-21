import { logger } from '../utils/logger';

export interface FinnhubNewsArticle {
  id: number;
  category: string;
  datetime: number;
  headline: string;
  image: string;
  related: string;
  source: string;
  summary: string;
  url: string;
}

export interface ExchangeRateData {
  base: string;
  time_last_update_unix: number;
  rates: Record<string, number>;
}

class MarketsService {
  private newsCache: { data: FinnhubNewsArticle[]; expiresAt: number } | null = null;
  private exchangeCache: { data: ExchangeRateData; expiresAt: number } | null = null;
  private readonly CACHE_TTL_MS = 5 * 60 * 1000; // 5 minutes

  async getFinancialNews(forceRefresh: boolean = false): Promise<FinnhubNewsArticle[]> {
    const now = Date.now();
    if (!forceRefresh && this.newsCache && this.newsCache.expiresAt > now) {
      logger.info('Returning cached financial news');
      return this.newsCache.data;
    }

    const apiKey = process.env.FINNHUB_API_KEY || 'd9frjd9r01qggl8lffhgd9frjd9r01qggl8lffi0';
    const url = `https://finnhub.io/api/v1/news?category=general&token=${apiKey}`;

    try {
      logger.info(`Fetching fresh financial news from Finnhub API (forceRefresh=${forceRefresh})`);
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error(`Finnhub API error HTTP ${response.status}`);
      }
      const data = await response.json() as FinnhubNewsArticle[];
      
      const filtered = Array.isArray(data) ? data.filter(item => item.headline && item.url) : [];

      this.newsCache = {
        data: filtered,
        expiresAt: now + this.CACHE_TTL_MS
      };

      return filtered;
    } catch (error) {
      logger.error('Failed to fetch Finnhub news:', error);
      if (this.newsCache) {
        logger.warn('Returning stale cached news due to API error');
        return this.newsCache.data;
      }
      return [];
    }
  }

  async getExchangeRates(forceRefresh: boolean = false): Promise<ExchangeRateData> {
    const now = Date.now();
    if (!forceRefresh && this.exchangeCache && this.exchangeCache.expiresAt > now) {
      logger.info('Returning cached exchange rates');
      return this.exchangeCache.data;
    }

    const url = 'https://open.er-api.com/v6/latest/USD';

    try {
      logger.info(`Fetching fresh exchange rates from ExchangeRate API (forceRefresh=${forceRefresh})`);
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error(`ExchangeRate API error HTTP ${response.status}`);
      }
      const data = await response.json() as ExchangeRateData;

      this.exchangeCache = {
        data,
        expiresAt: now + this.CACHE_TTL_MS
      };

      return data;
    } catch (error) {
      logger.error('Failed to fetch exchange rates:', error);
      if (this.exchangeCache) {
        return this.exchangeCache.data;
      }
      return {
        base: 'USD',
        time_last_update_unix: Math.floor(now / 1000),
        rates: {
          USD: 1.0,
          EUR: 0.92,
          GBP: 0.78,
          INR: 83.5,
          JPY: 155.2,
          CAD: 1.36,
          AUD: 1.51,
          CHF: 0.90,
          CNY: 7.25,
          SGD: 1.35,
          AED: 3.67
        }
      };
    }
  }
}

export const marketsService = new MarketsService();
