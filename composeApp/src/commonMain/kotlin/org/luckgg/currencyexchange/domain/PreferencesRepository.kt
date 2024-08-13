package org.luckgg.currencyexchange.domain

import kotlinx.coroutines.flow.Flow
import org.luckgg.currencyexchange.domain.model.CurrencyCode

interface PreferencesRepository {
    suspend fun saveLastUpdated(lastUpdated: String)

    suspend fun isDataFresh(currentTimestamp: Long): Boolean

    suspend fun saveSourceCurrencyCode(code: String)

    suspend fun saveTargetCurrencyCode(code: String)

    fun readSourceCurrencyCode(): Flow<CurrencyCode>

    fun readTargetCurrencyCode(): Flow<CurrencyCode>
}
