package org.luckgg.currencyexchange.di

import com.russhwolf.settings.Settings
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.luckgg.currencyexchange.data.local.MongoImpl
import org.luckgg.currencyexchange.data.local.PreferencesImpl
import org.luckgg.currencyexchange.data.remote.api.CurrencyApiServiceImpl
import org.luckgg.currencyexchange.domain.CurrencyApiService
import org.luckgg.currencyexchange.domain.MongoRepository
import org.luckgg.currencyexchange.domain.PreferencesRepository
import org.luckgg.currencyexchange.presentation.screen.HomeViewModel

val appModule =
    module {
        single { Settings() }
        single<MongoRepository> { MongoImpl() }
        single<PreferencesRepository> { PreferencesImpl(settings = get()) }
        single<CurrencyApiService> { CurrencyApiServiceImpl(preferences = get()) }
        factory {
            HomeViewModel(
                preferences = get(),
                mongoDb = get(),
                api = get(),
            )
        }
    }

fun initializeKoin() {
    startKoin {
        modules(appModule)
    }
}
