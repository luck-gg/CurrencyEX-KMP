package org.luckgg.currencyexchange.domain

interface PreferencesRepository {
    suspend fun saveLastUpdated(lastUpdated: String)

    suspend fun isDataFresh(currentTimestamp: Long): Boolean
}
