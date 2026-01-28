package com.faithflow.domain.usecase

import com.faithflow.domain.model.NewsItem
import com.faithflow.domain.repository.NewsItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Gets the latest news items, sorted by publish date (most recent first).
 * Optionally limits the number of items returned.
 */
class GetLatestNewsUseCase(
    private val newsItemRepository: NewsItemRepository
) {
    operator fun invoke(limit: Int? = null): Flow<List<NewsItem>> {
        return newsItemRepository.newsItems.map { newsItems ->
            val sorted = newsItems.sortedByDescending { it.publishDate }
            if (limit != null && limit > 0) {
                sorted.take(limit)
            } else {
                sorted
            }
        }
    }
}
