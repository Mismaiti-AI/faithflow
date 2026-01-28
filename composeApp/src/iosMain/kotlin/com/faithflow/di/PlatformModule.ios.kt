package com.faithflow.di

import com.faithflow.data.network.HttpClientFactory
import org.koin.dsl.module

actual fun platformModule() = module {
    // iOS-specific HttpClient with Darwin engine
    single {
        HttpClientFactory().create()
    }

    // Platform-specific dependencies will be added as needed
    // Examples: Database, File system, Keychain storage, etc.
}
