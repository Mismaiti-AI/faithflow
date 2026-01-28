package com.faithflow.data.repository

import com.faithflow.core.network.ApiResult
import com.faithflow.data.local.dao.EventDao
import com.faithflow.data.local.entity.toDomain
import com.faithflow.data.local.entity.toEntity
import com.faithflow.data.remote.GoogleSheetsService
import com.faithflow.domain.model.Event
import com.faithflow.domain.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventRepositoryImpl(
    private val googleSheetsService: GoogleSheetsService,
    private val eventDao: EventDao,
    private val sheetUrl: () -> String // Function to get current sheet URL
) : EventRepository {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    private val _selectedEvent = MutableStateFlow<Event?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    override val events: StateFlow<List<Event>> = _events.asStateFlow()
    override val selectedEvent: StateFlow<Event?> = _selectedEvent.asStateFlow()
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    override val error: StateFlow<String?> = _error.asStateFlow()

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        repositoryScope.launch {
            eventDao.observeAll().collect { entities ->
                _events.value = entities.map { it.toDomain() }
            }
        }
    }

    override suspend fun loadEvents(): ApiResult<Unit> {
        if (_events.value.isNotEmpty()) {
            return ApiResult.Success(Unit)
        }
        return refreshEvents()
    }

    override suspend fun refreshEvents(): ApiResult<Unit> {
        _isLoading.value = true
        _error.value = null

        return try {
            val url = sheetUrl()
            if (url.isBlank()) {
                throw Exception("Google Sheets URL not configured")
            }

            val events = googleSheetsService.fetchEvents(url)
            eventDao.replaceAll(events.map { it.toEntity() })

            _isLoading.value = false
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false

            val cached = eventDao.getAll()
            if (cached.isNotEmpty()) {
                _events.value = cached.map { it.toDomain() }
                _error.value = "Using cached data. ${e.message}"
                ApiResult.Success(Unit)
            } else {
                _error.value = e.message ?: "Failed to load events"
                ApiResult.Error(e)
            }
        }
    }

    override suspend fun selectEvent(eventId: String) {
        val event = _events.value.find { it.id == eventId }
        _selectedEvent.value = event
    }

    override fun clearSelection() {
        _selectedEvent.value = null
    }

    override suspend fun getEventById(id: String): ApiResult<Event> {
        val cached = _events.value.find { it.id == id }
        if (cached != null) {
            return ApiResult.Success(cached)
        }

        val local = eventDao.getById(id)
        return if (local != null) {
            ApiResult.Success(local.toDomain())
        } else {
            ApiResult.Error(Exception("Event not found: $id"))
        }
    }

    override fun clearError() {
        _error.value = null
    }
}
