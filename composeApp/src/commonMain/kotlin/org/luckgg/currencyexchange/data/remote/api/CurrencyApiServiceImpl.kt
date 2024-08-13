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
    companion object {
        const val ENDPOINT = "https://api.currencyapi.com/v3/latest"
        const val API_KEY = "cur_live_uZ9hYr7GlNBb2iEWp8tUejU7yLx7u6oZq6B4iskn"
    }

    private val httpClient =
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
                requestTimeoutMillis = 15000
            }
            install(DefaultRequest) {
                headers {
                    append("apikey", API_KEY)
                }
            }
        }

    override suspend fun getLatestExchangeRates(): RequestState<List<Currency>> =
        try {
            val response = httpClient.get(ENDPOINT)
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

                // Persist a timestamp
                val lastUpdated = apiResponse.meta.lastUpdatedAt
                preferences.saveLastUpdated(lastUpdated)

                RequestState.Success(data = availableCurrencies)
            } else {
                RequestState.Error(message = "HTTP Error Code: ${response.status}")
            }
        } catch (e: Exception) {
            RequestState.Error(message = e.message.toString())
        }
}
