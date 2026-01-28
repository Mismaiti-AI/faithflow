package com.faithflow.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class NewsItem(
    val id: String,
    val headline: String,
    val publishDate: Instant,
    val author: String,
    val body: String,
    val category: String,
    val scriptureReference: String,
    val isUrgent: Boolean,
    val photoURL: String,
    val relatedEventId: String
)
