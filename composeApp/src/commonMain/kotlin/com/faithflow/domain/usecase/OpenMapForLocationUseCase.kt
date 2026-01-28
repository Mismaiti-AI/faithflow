package com.faithflow.domain.usecase

import com.faithflow.core.network.ApiResult

/**
 * Prepares a map URL for opening the location in a maps application.
 * Generates a Google Maps URL with the location query.
 */
class OpenMapForLocationUseCase {
    suspend operator fun invoke(location: String): ApiResult<String> {
        if (location.isBlank()) {
            return ApiResult.Error(IllegalArgumentException("Location cannot be empty"))
        }

        // URL encode the location string for use in a map URL
        val encodedLocation = location.replace(" ", "+")
        val mapUrl = "https://www.google.com/maps/search/?api=1&query=$encodedLocation"

        return ApiResult.Success(mapUrl)
    }
}
