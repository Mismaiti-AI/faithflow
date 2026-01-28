package com.faithflow.data.repository

import com.faithflow.core.network.ApiResult
import com.faithflow.data.network.GoogleSheetsService
import com.faithflow.domain.model.Event
import com.faithflow.domain.repository.EventRepository
import com.faithflow.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementation of EventRepository using Google Sheets as data source
 * Provides in-memory caching with StateFlow for reactive updates
 */
class EventRepositoryImpl(
    private val googleSheetsService: GoogleSheetsService,
    private val preferencesRepository: PreferencesRepository
) : EventRepository {

    // ═══════════════════════════════════════════════════════════════
    // PRIVATE MUTABLE STATE
    // ═══════════════════════════════════════════════════════════════

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    private val _selectedEvent = MutableStateFlow<Event?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    // ═══════════════════════════════════════════════════════════════
    // PUBLIC IMMUTABLE STATE (Observable by ViewModels)
    // ═══════════════════════════════════════════════════════════════

    override val events: StateFlow<List<Event>> = _events.asStateFlow()
    override val selectedEvent: StateFlow<Event?> = _selectedEvent.asStateFlow()
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    override val error: StateFlow<String?> = _error.asStateFlow()

    // ═══════════════════════════════════════════════════════════════
    // LOAD / REFRESH
    // ═══════════════════════════════════════════════════════════════

    override suspend fun loadEvents(): ApiResult<Unit> {
        // Return cached if available
        if (_events.value.isNotEmpty()) {
            return ApiResult.Success(Unit)
        }
        return refreshEvents()
    }

    override suspend fun refreshEvents(): ApiResult<Unit> {
        _isLoading.value = true
        _error.value = null

        return try {
            val sheetUrl = preferencesRepository.getSheetUrl()
            if (sheetUrl.isNullOrBlank()) {
                _isLoading.value = false
                _error.value = "Google Sheets URL not configured"
                return ApiResult.Error(Exception("Sheet URL not configured"))
            }

            // Fetch events from Google Sheets
            val fetchedEvents = googleSheetsService.fetchEvents(sheetUrl)
            _events.value = fetchedEvents

            _isLoading.value = false
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            _error.value = e.message ?: "Failed to load events"
            ApiResult.Error(e)
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // SELECTION (For Detail Screens)
    // ═══════════════════════════════════════════════════════════════

    override suspend fun selectEvent(eventId: String) {
        val event = _events.value.find { it.id == eventId }
        _selectedEvent.value = event

        if (event == null) {
            _error.value = "Event not found: $eventId"
        }
    }

    override fun clearSelection() {
        _selectedEvent.value = null
    }

    // ═══════════════════════════════════════════════════════════════
    // FILTERING
    // ═══════════════════════════════════════════════════════════════

    override fun getEventsByCategory(category: String): List<Event> {
        return _events.value.filter { it.category.equals(category, ignoreCase = true) }
    }

    override fun getFeaturedEvents(): List<Event> {
        return _events.value.filter { it.isFeatured }
    }

    // ═══════════════════════════════════════════════════════════════
    // ERROR HANDLING
    // ═══════════════════════════════════════════════════════════════

    override fun clearError() {
        _error.value = null
    }
}
