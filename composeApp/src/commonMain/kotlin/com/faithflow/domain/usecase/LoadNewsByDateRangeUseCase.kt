package com.faithflow.domain.usecase

import com.faithflow.domain.model.NewsItem
import com.faithflow.domain.repository.NewsItemRepository
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Loads news items within a specified date range.
 * Returns news items where publishDate is between startDate and endDate (inclusive).
 */
@OptIn(ExperimentalTime::class)
class LoadNewsByDateRangeUseCase(
    private val newsItemRepository: NewsItemRepository
) {
    operator fun invoke(startDate: Instant, endDate: Instant): Flow<List<NewsItem>> {
        return newsItemRepository.newsItems.map { newsItems ->
            newsItems.filter { newsItem ->
                newsItem.publishDate >= startDate && newsItem.publishDate <= endDate
            }.sortedByDescending { it.publishDate }
        }
    }
}
