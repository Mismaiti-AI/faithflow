package com.faithflow.data.repository

import com.faithflow.core.network.ApiResult
import com.faithflow.data.local.dao.ChurchProfileDao
import com.faithflow.data.local.entity.toDomain
import com.faithflow.data.local.entity.toEntity
import com.faithflow.data.remote.GoogleSheetsService
import com.faithflow.domain.model.ChurchProfile
import com.faithflow.domain.repository.ChurchProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChurchProfileRepositoryImpl(
    private val googleSheetsService: GoogleSheetsService,
    private val churchProfileDao: ChurchProfileDao,
    private val sheetUrl: () -> String // Function to get current sheet URL
) : ChurchProfileRepository {

    private val _profile = MutableStateFlow<ChurchProfile?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    override val profile: StateFlow<ChurchProfile?> = _profile.asStateFlow()
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    override val error: StateFlow<String?> = _error.asStateFlow()

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        repositoryScope.launch {
            churchProfileDao.observe().collect { entity ->
                _profile.value = entity?.toDomain()
            }
        }
    }

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
            val url = sheetUrl()
            if (url.isBlank()) {
                throw Exception("Google Sheets URL not configured")
            }

            val profile = googleSheetsService.fetchChurchProfile(url)
            if (profile != null) {
                churchProfileDao.insert(profile.toEntity())
                _isLoading.value = false
                ApiResult.Success(Unit)
            } else {
                throw Exception("Failed to parse church profile from Google Sheets")
            }
        } catch (e: Exception) {
            _isLoading.value = false

            val cached = churchProfileDao.get()
            if (cached != null) {
                _profile.value = cached.toDomain()
                _error.value = "Using cached data. ${e.message}"
                ApiResult.Success(Unit)
            } else {
                _error.value = e.message ?: "Failed to load church profile"
                ApiResult.Error(e)
            }
        }
    }

    override suspend fun updateProfile(profile: ChurchProfile): ApiResult<Unit> {
        return try {
            churchProfileDao.insert(profile.toEntity())
            _profile.value = profile
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to update church profile"
            ApiResult.Error(e)
        }
    }

    override fun clearError() {
        _error.value = null
    }
}
