package com.faithflow.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository for app preferences and settings
 * Stores configuration like Google Sheets URL
 */
interface PreferencesRepository {
    suspend fun saveSheetUrl(url: String)
    suspend fun getSheetUrl(): String?
    fun observeSheetUrl(): Flow<String?>

    suspend fun saveChurchName(name: String)
    suspend fun getChurchName(): String?

    suspend fun clearAll()
}
