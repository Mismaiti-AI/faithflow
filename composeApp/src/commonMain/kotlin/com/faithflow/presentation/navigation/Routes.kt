package com.faithflow.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes for FaithFlow
 * Each route represents a destination in the app
 */

@Serializable
object Setup

@Serializable
object Home

@Serializable
object EventCalendar

@Serializable
data class EventDetail(val eventId: String)

@Serializable
object NewsFeed

@Serializable
data class NewsDetail(val newsId: String)

@Serializable
object ChurchProfile

@Serializable
object Settings
