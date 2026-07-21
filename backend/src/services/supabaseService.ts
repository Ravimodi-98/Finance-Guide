import { createClient } from '@supabase/supabase-js';

const supabaseUrl = process.env.SUPABASE_URL || 'https://cwmomyufrmodisbvblob.supabase.co';
const supabaseKey = process.env.SUPABASE_KEY || 'sb_publishable_wkKF87M2jW8HTqfI44bqQQ_rBCU_vg0';

export const supabase = createClient(supabaseUrl, supabaseKey, {
  auth: {
    autoRefreshToken: false,
    persistSession: false
  }
});
