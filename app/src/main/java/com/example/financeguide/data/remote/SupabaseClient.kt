package com.example.financeguide.data.remote

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object SupabaseClient {
    private const val BASE_URL = "https://gqdqqrtbdrsbvcdtpnpo.supabase.co/"
    const val API_KEY = "sb_publishable_yDGTSXRw3To1S2A1AtTCxA_gq4LGwmK"

    private val moshi = Moshi.Builder().build()

    val api: SupabaseApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SupabaseApi::class.java)
    }
}
