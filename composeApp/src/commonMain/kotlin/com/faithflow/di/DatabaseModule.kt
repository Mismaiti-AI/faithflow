package com.faithflow.di

import com.faithflow.core.database.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

val databaseModule = module {
    single {
        getAppDatabase()
    }

    single {
        get<AppDatabase>().eventDao()
    }

    single {
        get<AppDatabase>().newsItemDao()
    }

    single {
        get<AppDatabase>().churchProfileDao()
    }
}

expect fun getAppDatabase(): AppDatabase
