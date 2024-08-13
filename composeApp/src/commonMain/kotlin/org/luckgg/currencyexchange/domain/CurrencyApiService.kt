package org.luckgg.currencyexchange.domain

import org.luckgg.currencyexchange.domain.model.Currency
import org.luckgg.currencyexchange.domain.model.RequestState

fun interface CurrencyApiService {
    suspend fun getLatestExchangeRates(): RequestState<List<Currency>>
}
