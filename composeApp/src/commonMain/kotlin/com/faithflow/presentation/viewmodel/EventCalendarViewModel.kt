package com.faithflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithflow.domain.model.Event
import com.faithflow.domain.repository.EventRepository
import com.faithflow.domain.repository.PreferencesRepository
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EventCalendarViewModel(
    private val repository: EventRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val uiState: StateFlow<EventCalendarUiState> = combine(
        repository.events,
        repository.isLoading,
        repository.error,
        _searchQuery,
        _selectedCategory
    ) { events, isLoading, error, query, category ->
        val filteredEvents = filterEvents(events, query, category)
        EventCalendarUiState(
            events = filteredEvents,
            allCategories = events.map { it.category }.distinct().sorted(),
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EventCalendarUiState()
    )

    init {
        loadEvents()
    }

    private fun filterEvents(
        events: List<Event>,
        query: String,
        category: String?
    ): List<Event> {
        var filtered = events

        // Filter by category
        if (category != null) {
            filtered = filtered.filter { it.category.equals(category, ignoreCase = true) }
        }

        // Filter by search query
        if (query.isNotBlank()) {
            filtered = filtered.filter { event ->
                event.title.contains(query, ignoreCase = true) ||
                        event.description.contains(query, ignoreCase = true) ||
                        event.location.contains(query, ignoreCase = true)
            }
        }

        return filtered
    }

    fun loadEvents() {
        viewModelScope.launch {
            repository.loadEvents()
        }
    }

    fun refreshEvents() {
        viewModelScope.launch {
            repository.refreshEvents()
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun clearError() {
        repository.clearError()
    }
}

data class EventCalendarUiState(
    val events: List<Event> = emptyList(),
    val allCategories: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
