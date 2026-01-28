package com.faithflow.domain.repository

import com.faithflow.core.network.ApiResult
import com.faithflow.domain.model.Event
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository for Event data
 * Owns shared state accessible by all ViewModels
 */
interface EventRepository {
    // ═══════════════════════════════════════════════════════════════
    // SHARED STATE - Observable by ALL ViewModels
    // ═══════════════════════════════════════════════════════════════

    /** All events list - shared across all screens */
    val events: StateFlow<List<Event>>

    /** Currently selected/focused event */
    val selectedEvent: StateFlow<Event?>

    /** Loading state */
    val isLoading: StateFlow<Boolean>

    /** Error state */
    val error: StateFlow<String?>

    // ═══════════════════════════════════════════════════════════════
    // ACTIONS - Called by ViewModels to modify state
    // ═══════════════════════════════════════════════════════════════

    /** Load events (uses cache if available) */
    suspend fun loadEvents(): ApiResult<Unit>

    /** Force refresh from remote */
    suspend fun refreshEvents(): ApiResult<Unit>

    /** Select event by ID (for detail screens) */
    suspend fun selectEvent(eventId: String)

    /** Clear current selection */
    fun clearSelection()

    /** Get events by category */
    fun getEventsByCategory(category: String): List<Event>

    /** Get featured events */
    fun getFeaturedEvents(): List<Event>

    /** Clear error state */
    fun clearError()
}
