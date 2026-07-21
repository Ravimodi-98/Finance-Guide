import { Request, Response } from 'express';
import { marketsService } from '../services/marketsService';

export const getNews = async (req: Request, res: Response) => {
  try {
    const forceRefresh = req.query.force === 'true';
    const news = await marketsService.getFinancialNews(forceRefresh);
    return res.status(200).json({
      success: true,
      count: news.length,
      data: news
    });
  } catch (error: any) {
    return res.status(500).json({
      success: false,
      error: 'NEWS_FETCH_ERROR',
      message: 'Failed to fetch financial news.'
    });
  }
};

export const getExchangeRates = async (req: Request, res: Response) => {
  try {
    const forceRefresh = req.query.force === 'true';
    const exchange = await marketsService.getExchangeRates(forceRefresh);
    return res.status(200).json({
      success: true,
      data: exchange
    });
  } catch (error: any) {
    return res.status(500).json({
      success: false,
      error: 'EXCHANGE_FETCH_ERROR',
      message: 'Failed to fetch exchange rates.'
    });
  }
};
