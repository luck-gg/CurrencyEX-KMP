package org.luckgg.currencyexchange.domain

import org.luckgg.currencyexchange.domain.model.Currency
import org.luckgg.currencyexchange.domain.model.RequestState

interface CurrencyApiService {
    suspend fun getLatestRates(): RequestState<List<Currency>>
}
