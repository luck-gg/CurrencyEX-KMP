package org.luckgg.currencyexchange.domain

import kotlinx.coroutines.flow.Flow
import org.luckgg.currencyexchange.domain.model.Currency
import org.luckgg.currencyexchange.domain.model.RequestState

interface MongoRepository {
    fun configureTheRealm()

    suspend fun insertCurrencyData(currency: Currency)

    fun readCurrencyData(): Flow<RequestState<List<Currency>>>

    suspend fun cleanUp()
}
