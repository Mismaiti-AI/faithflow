package com.faithflow.domain.usecase

import com.faithflow.core.network.ApiResult
import com.faithflow.domain.repository.EventRepository

/**
 * Gets the full description of a specific event.
 * Returns the description string from the event.
 */
class GetEventDescriptionUseCase(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(eventId: String): ApiResult<String> {
        return when (val result = eventRepository.getEventById(eventId)) {
            is ApiResult.Success -> {
                ApiResult.Success(result.data.description)
            }
            is ApiResult.Error -> result
        }
    }
}
