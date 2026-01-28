package com.faithflow.di

import com.faithflow.data.network.GoogleSheetsService
import com.faithflow.data.repository.ChurchProfileRepositoryImpl
import com.faithflow.data.repository.EventRepositoryImpl
import com.faithflow.data.repository.NewsRepositoryImpl
import com.faithflow.data.repository.PreferencesRepositoryImpl
import com.faithflow.domain.repository.ChurchProfileRepository
import com.faithflow.domain.repository.EventRepository
import com.faithflow.domain.repository.NewsRepository
import com.faithflow.domain.repository.PreferencesRepository
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Main application module
 * Contains repositories, use cases, and ViewModels
 */
val appModule = module {
    // ═══════════════════════════════════════════════════════════════
    // Services
    // ═══════════════════════════════════════════════════════════════
    single { GoogleSheetsService(get()) }

    // ═══════════════════════════════════════════════════════════════
    // Repositories (MUST BE SINGLETON for shared state!)
    // ═══════════════════════════════════════════════════════════════
    single<PreferencesRepository> { PreferencesRepositoryImpl() }
    single<EventRepository> { EventRepositoryImpl(get(), get()) }
    single<NewsRepository> { NewsRepositoryImpl(get(), get()) }
    single<ChurchProfileRepository> { ChurchProfileRepositoryImpl(get()) }

    // ═══════════════════════════════════════════════════════════════
    // Use cases will be added in next phase
    // ViewModels will be added in Phase 6
    // ═══════════════════════════════════════════════════════════════
}

/**
 * Platform-specific module - each platform provides its own implementation
 * Contains HttpClient engines, database instances, and platform-specific dependencies
 */
expect fun platformModule(): Module

/**
 * Helper function to get all modules
 */
fun allModules() = listOf(
    appModule,
    platformModule()
)
