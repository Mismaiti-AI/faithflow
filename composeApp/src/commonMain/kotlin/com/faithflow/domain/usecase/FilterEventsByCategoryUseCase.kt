package com.faithflow.domain.usecase

import com.faithflow.domain.model.Event
import com.faithflow.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Filters events by category.
 * Returns a flow of events that match the specified category.
 */
class FilterEventsByCategoryUseCase(
    private val eventRepository: EventRepository
) {
    operator fun invoke(category: String): Flow<List<Event>> {
        return eventRepository.events.map { events ->
            if (category.isBlank()) {
                events
            } else {
                events.filter { event ->
                    event.category.equals(category, ignoreCase = true)
                }
            }
        }
    }
}
