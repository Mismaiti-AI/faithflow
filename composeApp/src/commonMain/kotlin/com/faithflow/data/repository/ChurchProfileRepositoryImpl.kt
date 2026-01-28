package com.faithflow.data.repository

import com.faithflow.core.config.GoogleSheetsConfig
import com.faithflow.core.network.ApiResult
import com.faithflow.data.network.GoogleSheetsService
import com.faithflow.domain.model.ChurchProfile
import com.faithflow.domain.repository.ChurchProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChurchProfileRepositoryImpl(
    private val googleSheetsService: GoogleSheetsService
) : ChurchProfileRepository {

    private val _profile = MutableStateFlow<ChurchProfile?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    override val profile: StateFlow<ChurchProfile?> = _profile.asStateFlow()
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    override val error: StateFlow<String?> = _error.asStateFlow()

    override suspend fun loadProfile(): ApiResult<Unit> {
        if (_profile.value != null) {
            return ApiResult.Success(Unit)
        }
        return refreshProfile()
    }

    override suspend fun refreshProfile(): ApiResult<Unit> {
        _isLoading.value = true
        _error.value = null

        return try {
            val fetchedProfile = googleSheetsService.fetchChurchProfile(GoogleSheetsConfig.CHURCH_PROFILE_CSV_URL)
            _profile.value = fetchedProfile

            _isLoading.value = false
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            _error.value = e.message ?: "Failed to load church profile"
            ApiResult.Error(e)
        }
    }

    override fun clearError() {
        _error.value = null
    }
}
