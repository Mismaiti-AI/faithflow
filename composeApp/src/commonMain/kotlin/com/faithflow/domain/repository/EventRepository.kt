package com.faithflow.domain.repository

import com.faithflow.core.network.ApiResult
import com.faithflow.domain.model.Event
import kotlinx.coroutines.flow.StateFlow

interface EventRepository {
    val events: StateFlow<List<Event>>
    val selectedEvent: StateFlow<Event?>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>

    suspend fun loadEvents(): ApiResult<Unit>
    suspend fun refreshEvents(): ApiResult<Unit>
    suspend fun selectEvent(eventId: String)
    fun clearSelection()
    suspend fun getEventById(id: String): ApiResult<Event>
    fun clearError()
}
