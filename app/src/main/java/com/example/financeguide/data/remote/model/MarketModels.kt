package com.example.financeguide.data.remote.model

import com.google.gson.annotations.SerializedName

data class FinnhubNewsArticleDto(
    @SerializedName("id") val id: Long,
    @SerializedName("category") val category: String?,
    @SerializedName("datetime") val datetime: Long,
    @SerializedName("headline") val headline: String,
    @SerializedName("image") val image: String?,
    @SerializedName("related") val related: String?,
    @SerializedName("source") val source: String?,
    @SerializedName("summary") val summary: String?,
    @SerializedName("url") val url: String
)

data class NewsResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("count") val count: Int?,
    @SerializedName("data") val data: List<FinnhubNewsArticleDto>?
)

data class ExchangeRateResponseData(
    @SerializedName("base") val base: String?,
    @SerializedName("time_last_update_unix") val timeLastUpdateUnix: Long?,
    @SerializedName("rates") val rates: Map<String, Double>?
)

data class ExchangeResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ExchangeRateResponseData?
)

data class CurrencyRateItem(
    val code: String,
    val name: String,
    val flag: String,
    val rate: Double,
    val isAppreciating: Boolean
)
