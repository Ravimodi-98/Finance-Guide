package com.example.financeguide.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseClient {
    private const val BASE_URL = "https://gqdqqrtbdrsbvcdtpnpo.supabase.co/"
    const val API_KEY = "sb_publishable_yDGTSXRw3To1S2A1AtTCxA_gq4LGwmK"

    val api: SupabaseApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseApi::class.java)
    }
}
