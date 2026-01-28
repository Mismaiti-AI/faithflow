package com.faithflow.domain.usecase

import com.faithflow.core.network.ApiResult
import com.faithflow.domain.model.ChurchProfile
import com.faithflow.domain.repository.ChurchProfileRepository

/**
 * Updates the church profile information.
 * Delegates to the ChurchProfileRepository to persist the changes.
 */
class UpdateChurchProfileUseCase(
    private val churchProfileRepository: ChurchProfileRepository
) {
    suspend operator fun invoke(profile: ChurchProfile): ApiResult<Unit> {
        // Validate profile data
        if (profile.name.isBlank()) {
            return ApiResult.Error(
                IllegalArgumentException("Church name cannot be empty")
            )
        }

        // Delegate to repository to update the profile
        return churchProfileRepository.updateProfile(profile)
    }
}
