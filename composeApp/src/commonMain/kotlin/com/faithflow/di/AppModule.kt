package com.faithflow.di

import com.faithflow.data.repository.ChurchProfileRepositoryImpl
import com.faithflow.data.repository.EventRepositoryImpl
import com.faithflow.data.repository.NewsItemRepositoryImpl
import com.faithflow.data.repository.PreferencesRepositoryImpl
import com.faithflow.domain.repository.ChurchProfileRepository
import com.faithflow.domain.repository.EventRepository
import com.faithflow.domain.repository.NewsItemRepository
import com.faithflow.domain.repository.PreferencesRepository
import com.faithflow.domain.usecase.ApplyCategoryFilterUseCase
import com.faithflow.domain.usecase.FetchEventLocationUseCase
import com.faithflow.domain.usecase.FilterEventsByCategoryUseCase
import com.faithflow.domain.usecase.GetEventDescriptionUseCase
import com.faithflow.domain.usecase.GetLatestNewsUseCase
import com.faithflow.domain.usecase.GetUpcomingEventsUseCase
import com.faithflow.domain.usecase.LoadNewsByDateRangeUseCase
import com.faithflow.domain.usecase.MarkNewsAsReadUseCase
import com.faithflow.domain.usecase.OpenMapForLocationUseCase
import com.faithflow.domain.usecase.ResetFiltersUseCase
import com.faithflow.domain.usecase.SavePreferredCategoriesUseCase
import com.faithflow.domain.usecase.SearchEventsUseCase
import com.faithflow.domain.usecase.SetGoogleSheetUrlUseCase
import com.faithflow.domain.usecase.TestSheetConnectionUseCase
import com.faithflow.domain.usecase.UpdateChurchProfileUseCase
import com.faithflow.presentation.viewmodel.AdminConfigViewModel
import com.faithflow.presentation.viewmodel.ChurchProfileViewModel
import com.faithflow.presentation.viewmodel.EventCalendarViewModel
import com.faithflow.presentation.viewmodel.EventCategoryFilterViewModel
import com.faithflow.presentation.viewmodel.EventDetailViewModel
import com.faithflow.presentation.viewmodel.NewsDetailViewModel
import com.faithflow.presentation.viewmodel.NewsFeedViewModel
import com.faithflow.presentation.viewmodel.OnboardingViewModel
import com.faithflow.presentation.viewmodel.SettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    // Repositories (Singletons)
    singleOf(::PreferencesRepositoryImpl) bind PreferencesRepository::class

    single<EventRepository> {
        EventRepositoryImpl(
            googleSheetsService = get(),
            eventDao = get(),
            sheetUrl = { get<PreferencesRepository>().sheetUrl.value }
        )
    }

    single<NewsItemRepository> {
        NewsItemRepositoryImpl(
            googleSheetsService = get(),
            newsItemDao = get(),
            sheetUrl = { get<PreferencesRepository>().sheetUrl.value }
        )
    }

    single<ChurchProfileRepository> {
        ChurchProfileRepositoryImpl(
            googleSheetsService = get(),
            churchProfileDao = get(),
            sheetUrl = { get<PreferencesRepository>().sheetUrl.value }
        )
    }

    // Use Cases (Factories)
    factoryOf(::GetUpcomingEventsUseCase)
    factoryOf(::FilterEventsByCategoryUseCase)
    factoryOf(::SearchEventsUseCase)
    factoryOf(::GetLatestNewsUseCase)
    factoryOf(::MarkNewsAsReadUseCase)
    factoryOf(::LoadNewsByDateRangeUseCase)
    factoryOf(::FetchEventLocationUseCase)
    factoryOf(::GetEventDescriptionUseCase)
    factoryOf(::OpenMapForLocationUseCase)
    factoryOf(::ApplyCategoryFilterUseCase)
    factoryOf(::SavePreferredCategoriesUseCase)
    factoryOf(::ResetFiltersUseCase)
    factoryOf(::SetGoogleSheetUrlUseCase)
    factoryOf(::TestSheetConnectionUseCase)
    factoryOf(::UpdateChurchProfileUseCase)

    // ViewModels
    viewModelOf(::AdminConfigViewModel)
    viewModelOf(::ChurchProfileViewModel)
    viewModelOf(::EventCalendarViewModel)
    viewModelOf(::EventCategoryFilterViewModel)
    viewModelOf(::NewsFeedViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::SettingsViewModel)

    // ViewModels with parameters
    viewModel { (eventId: String) ->
        EventDetailViewModel(eventId = eventId, repository = get())
    }

    viewModel { (newsItemId: String) ->
        NewsDetailViewModel(newsItemId = newsItemId, repository = get())
    }
}
