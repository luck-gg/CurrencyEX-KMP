package org.luckgg.currencyexchange.di

import com.russhwolf.settings.Settings
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.luckgg.currencyexchange.data.local.PreferencesRepositoryImpl
import org.luckgg.currencyexchange.data.remote.api.CurrencyApiServiceImpl
import org.luckgg.currencyexchange.domain.CurrencyApiService
import org.luckgg.currencyexchange.domain.PreferencesRepository
import org.luckgg.currencyexchange.presentation.screen.HomeViewModel

class KoinModule {
    private val appModule =
        module {
            single { Settings() }
            single<PreferencesRepository> { PreferencesRepositoryImpl(settings = get()) }
            single<CurrencyApiService> { CurrencyApiServiceImpl(preferences = get()) }
            factory {
                HomeViewModel(preferences = get(), api = get())
            }
        }

    fun initializeKoin() {
        startKoin {
            modules(appModule)
        }
    }
}
