package com.faithflow.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Event(
    val id: String = "",
    val title: String = "",
    val date: Instant = Instant.DISTANT_PAST,
    val category: String = "",
    val location: String = "",
    val description: String = "",
    val topic: String = "",
    val bibleVerse: String = "",
    val picCouncilMember: String = "",
    val onDutyCouncilMembers: List<String> = emptyList(),
    val isFeatured: Boolean = false
)
