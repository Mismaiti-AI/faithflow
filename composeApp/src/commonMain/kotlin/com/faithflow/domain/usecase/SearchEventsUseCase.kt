package com.faithflow.domain.usecase

import com.faithflow.domain.model.Event
import com.faithflow.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Searches events by query string.
 * Searches in title, description, location, and topic fields.
 */
class SearchEventsUseCase(
    private val eventRepository: EventRepository
) {
    operator fun invoke(query: String): Flow<List<Event>> {
        return eventRepository.events.map { events ->
            if (query.isBlank()) {
                events
            } else {
                events.filter { event ->
                    event.title.contains(query, ignoreCase = true) ||
                    event.description.contains(query, ignoreCase = true) ||
                    event.location.contains(query, ignoreCase = true) ||
                    event.topic.contains(query, ignoreCase = true)
                }
            }
        }
    }
}
