package com.faithflow.core.config

/**
 * Google Sheets Configuration for FaithFlow
 *
 * Setup Instructions:
 * 1. Create a Google Sheet with tabs for: Events, News, ChurchProfile
 * 2. Go to File → Share → Publish to web
 * 3. Select each tab and publish as CSV
 * 4. Copy the generated URLs and replace the placeholders below
 *
 * The URLs should look like:
 * https://docs.google.com/spreadsheets/d/YOUR_SHEET_ID/export?format=csv&gid=TAB_GID
 */
object GoogleSheetsConfig {
    // Replace these with your actual published CSV URLs
    const val EVENTS_CSV_URL = "https://docs.google.com/spreadsheets/d/REPLACE_WITH_YOUR_SHEET_ID/export?format=csv&gid=0"
    const val NEWS_CSV_URL = "https://docs.google.com/spreadsheets/d/REPLACE_WITH_YOUR_SHEET_ID/export?format=csv&gid=1"
    const val CHURCH_PROFILE_CSV_URL = "https://docs.google.com/spreadsheets/d/REPLACE_WITH_YOUR_SHEET_ID/export?format=csv&gid=2"

    // Auto-refresh interval (in minutes)
    const val REFRESH_INTERVAL_MINUTES = 30
}
