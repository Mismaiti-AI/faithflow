package com.faithflow.domain.usecase

import com.faithflow.core.network.ApiResult

/**
 * Saves user's preferred categories for filtering.
 * This is a placeholder implementation for future preferences functionality.
 * In a full implementation, this would save to shared preferences.
 */
class SavePreferredCategoriesUseCase {
    suspend operator fun invoke(categories: Set<String>): ApiResult<Unit> {
        // Placeholder: In a full implementation, this would:
        // 1. Save categories to shared preferences or local database
        // 2. Validate category names
        // 3. Handle persistence errors

        // For now, just return success
        return ApiResult.Success(Unit)
    }
}
