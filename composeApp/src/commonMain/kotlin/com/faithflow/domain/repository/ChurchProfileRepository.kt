package com.faithflow.domain.repository

import com.faithflow.core.network.ApiResult
import com.faithflow.domain.model.ChurchProfile
import kotlinx.coroutines.flow.StateFlow

interface ChurchProfileRepository {
    val profile: StateFlow<ChurchProfile?>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>

    suspend fun loadProfile(): ApiResult<Unit>
    suspend fun refreshProfile(): ApiResult<Unit>
    suspend fun updateProfile(profile: ChurchProfile): ApiResult<Unit>
    fun clearError()
}
