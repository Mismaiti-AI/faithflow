package com.faithflow.data.repository

import com.faithflow.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Simple in-memory implementation of PreferencesRepository
 * TODO: Replace with DataStore for persistent storage
 */
class PreferencesRepositoryImpl : PreferencesRepository {
    private val _sheetUrl = MutableStateFlow<String?>(null)
    private val _churchName = MutableStateFlow<String?>(null)

    override suspend fun saveSheetUrl(url: String) {
        _sheetUrl.value = url
    }

    override suspend fun getSheetUrl(): String? {
        return _sheetUrl.value
    }

    override fun observeSheetUrl(): Flow<String?> {
        return _sheetUrl.asStateFlow()
    }

    override suspend fun saveChurchName(name: String) {
        _churchName.value = name
    }

    override suspend fun getChurchName(): String? {
        return _churchName.value
    }

    override suspend fun clearAll() {
        _sheetUrl.value = null
        _churchName.value = null
    }
}
