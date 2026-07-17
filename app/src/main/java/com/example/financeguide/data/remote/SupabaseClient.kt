package com.example.financeguide.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseClient {
    private const val BASE_URL = "https://cwmomyufrmodisbvblob.supabase.co/"
    const val API_KEY = "sb_publishable_wkKF87M2jW8HTqfI44bqQQ_rBCU_vg0"

    val api: SupabaseApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseApi::class.java)
    }
}
