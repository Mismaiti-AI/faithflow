package com.faithflow.domain.usecase

import com.faithflow.domain.model.Event
import com.faithflow.domain.repository.EventRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Gets upcoming events (events with date in the future).
 * Filters events from the repository to only include future events.
 */
@OptIn(ExperimentalTime::class)
class GetUpcomingEventsUseCase(
    private val eventRepository: EventRepository
) {
    operator fun invoke(): Flow<List<Event>> {
        val now = Clock.System.now()
        return eventRepository.events.map { events ->
            events.filter { event ->
                event.date > now
            }.sortedBy { it.date }
        }
    }
}
