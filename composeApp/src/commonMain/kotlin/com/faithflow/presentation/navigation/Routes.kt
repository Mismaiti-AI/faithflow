package com.faithflow.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object Onboarding

@Serializable
object EventCalendar

@Serializable
data class EventDetail(val eventId: String)

@Serializable
object EventCategoryFilter

@Serializable
object NewsFeed

@Serializable
data class NewsDetail(val newsItemId: String)

@Serializable
object ChurchProfile

@Serializable
object AdminConfig

@Serializable
object Settings
