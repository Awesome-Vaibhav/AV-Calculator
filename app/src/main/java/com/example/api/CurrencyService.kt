package com.example.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

@JsonClass(generateAdapter = true)
data class CurrencyResponse(
    @Json(name = "result") val result: String,
    @Json(name = "time_last_update_utc") val timeLastUpdateUtc: String?,
    @Json(name = "base_code") val baseCode: String,
    @Json(name = "rates") val rates: Map<String, Double>
)

interface CurrencyService {
    @GET("v6/latest/USD")
    suspend fun getUsdRates(): CurrencyResponse

    companion object {
        private const val BASE_URL = "https://open.er-api.com/"

        fun create(): CurrencyService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
            return retrofit.create(CurrencyService::class.java)
        }
    }
}
