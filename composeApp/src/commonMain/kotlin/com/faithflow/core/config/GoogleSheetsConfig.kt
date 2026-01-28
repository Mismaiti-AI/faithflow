package com.faithflow.core.config

object GoogleSheetsConfig {
    // These URLs will be configured during onboarding
    // For now, using placeholder - will be replaced via preferences
    const val DEFAULT_SHEET_URL = ""

    // Refresh interval (30 minutes default)
    const val REFRESH_INTERVAL_MINUTES = 30

    // Tab/GID identifiers (will be discovered dynamically)
    const val EVENTS_TAB_NAME = "Events"
    const val NEWS_TAB_NAME = "News"
    const val PROFILE_TAB_NAME = "Profile"
}
