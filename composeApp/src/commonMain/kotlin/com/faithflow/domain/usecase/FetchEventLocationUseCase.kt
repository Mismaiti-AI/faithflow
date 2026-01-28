package com.faithflow.domain.usecase

import com.faithflow.core.network.ApiResult
import com.faithflow.domain.repository.EventRepository

/**
 * Fetches the location details for a specific event.
 * Returns the location string from the event.
 */
class FetchEventLocationUseCase(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(eventId: String): ApiResult<String> {
        return when (val result = eventRepository.getEventById(eventId)) {
            is ApiResult.Success -> {
                ApiResult.Success(result.data.location)
            }
            is ApiResult.Error -> result
        }
    }
}
