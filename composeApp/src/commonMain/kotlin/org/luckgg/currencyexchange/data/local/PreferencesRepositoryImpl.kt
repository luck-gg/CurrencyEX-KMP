package org.luckgg.currencyexchange.data.local

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.luckgg.currencyexchange.domain.PreferencesRepository

class PreferencesRepositoryImpl(
    settings: Settings,
) : PreferencesRepository {
    companion object {
        const val TIMESTAMP_KEY = "lastUpdated"
        const val SOURCE_CURRENCY_KEY = "sourceCurrency"
        const val TARGET_CURRENCY_KEY = "targetCurrency"
    }

    private val flowSettings: FlowSettings = (settings as ObservableSettings).toFlowSettings()

    override suspend fun saveLastUpdated(lastUpdated: String) {
        flowSettings.putLong(
            key = TIMESTAMP_KEY,
            value = Instant.parse(lastUpdated).toEpochMilliseconds(),
        )
    }

    override suspend fun isDataFresh(currentTimestamp: Long): Boolean {
        val savedTimestamp =
            flowSettings.getLong(
                key = TIMESTAMP_KEY,
                defaultValue = 0L,
            )

        return if (savedTimestamp != 0L) {
            val currentInstant = Instant.fromEpochMilliseconds(currentTimestamp)
            val savedInstant = Instant.fromEpochMilliseconds(savedTimestamp)

            val currentDateTime =
                currentInstant
                    .toLocalDateTime(TimeZone.currentSystemDefault())
            val savedDateTime =
                savedInstant
                    .toLocalDateTime(TimeZone.currentSystemDefault())

            val daysDifference = currentDateTime.date.dayOfYear - savedDateTime.date.dayOfYear
            daysDifference < 1
        } else {
            false
        }
    }
}
