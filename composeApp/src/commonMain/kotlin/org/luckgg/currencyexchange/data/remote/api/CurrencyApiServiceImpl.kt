package org.luckgg.currencyexchange.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.luckgg.currencyexchange.domain.CurrencyApiService
import org.luckgg.currencyexchange.domain.PreferencesRepository
import org.luckgg.currencyexchange.domain.model.ApiResponse
import org.luckgg.currencyexchange.domain.model.Currency
import org.luckgg.currencyexchange.domain.model.CurrencyCode
import org.luckgg.currencyexchange.domain.model.RequestState

class CurrencyApiServiceImpl(
    private val preferences: PreferencesRepository,
) : CurrencyApiService {
    override suspend fun getLatestRates(): RequestState<List<Currency>> =
        try {
            val response = client.get(BASE_URL)
            if (response.status.value == 200) {
                val apiResponse = Json.decodeFromString<ApiResponse>(response.body())
                val availableCurrencyCodes =
                    apiResponse.data.keys
                        .filter {
                            CurrencyCode.entries
                                .map { code -> code.name }
                                .toSet()
                                .contains(it)
                        }

                val availableCurrencies =
                    apiResponse.data.values
                        .filter { currency ->
                            availableCurrencyCodes.contains(currency.code)
                        }

                val lastUpdated = apiResponse.meta.lastUpdated
                preferences.saveLastUpdated(lastUpdated)

                RequestState.Success(data = availableCurrencies)
            } else {
                RequestState.Error(message = "HTTP Error Code: ${response.status}")
            }
        } catch (e: Exception) {
            RequestState.Error(message = e.message.toString())
        }

    private val client =
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    },
                )
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15000L
            }
            install(DefaultRequest) {
                headers {
                    append("apikey", API_KEY)
                }
            }
        }

    companion object {
        private const val BASE_URL = "https://api.currencyapi.com/v3/latest"
        private const val API_KEY = "cur_live_uZ9hYr7GlNBb2iEWp8tUejU7yLx7u6oZq6B4iskn"
    }
}
