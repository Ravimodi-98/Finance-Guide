import { Router } from 'express';
import { getNews, getExchangeRates } from '../controllers/marketsController';

const router = Router();

router.get('/news', getNews);
router.get('/exchange', getExchangeRates);

export default router;
