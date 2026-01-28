package com.faithflow.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.dsl.module

actual fun platformModule() = module {
    single<Settings> {
        val context = get<Context>()
        SharedPreferencesSettings(
            context.getSharedPreferences("faithflow_prefs", android.content.Context.MODE_PRIVATE)
        )
    }
}
