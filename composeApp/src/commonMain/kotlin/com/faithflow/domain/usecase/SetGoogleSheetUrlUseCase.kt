package com.faithflow.domain.usecase

import com.faithflow.core.network.ApiResult

/**
 * Saves the Google Sheet URL to preferences.
 * Validates the URL format before saving.
 * This is a placeholder implementation for future preferences functionality.
 */
class SetGoogleSheetUrlUseCase {
    suspend operator fun invoke(sheetUrl: String): ApiResult<Unit> {
        // Validate URL
        if (sheetUrl.isBlank()) {
            return ApiResult.Error(IllegalArgumentException("Sheet URL cannot be empty"))
        }

        if (!isValidGoogleSheetUrl(sheetUrl)) {
            return ApiResult.Error(
                IllegalArgumentException("Invalid Google Sheets URL format")
            )
        }

        // Placeholder: In a full implementation, this would:
        // 1. Save URL to shared preferences
        // 2. Update GoogleSheetsConfig
        // 3. Trigger data reload from new sheet

        return ApiResult.Success(Unit)
    }

    private fun isValidGoogleSheetUrl(url: String): Boolean {
        return url.contains("docs.google.com/spreadsheets") ||
               url.contains("docs.google.com/spreadsheets/d/") ||
               url.contains("drive.google.com/file/d/")
    }
}
