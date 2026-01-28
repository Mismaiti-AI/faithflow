package com.faithflow.domain.usecase

import com.faithflow.domain.model.Event
import com.faithflow.domain.model.NewsItem

/**
 * Applies category filter to a list of items (events or news).
 * This use case contains the business logic for filtering by multiple categories.
 */
class ApplyCategoryFilterUseCase {
    /**
     * Filters events by the selected categories.
     * If no categories are selected, returns all events.
     */
    fun filterEvents(events: List<Event>, selectedCategories: Set<String>): List<Event> {
        if (selectedCategories.isEmpty()) {
            return events
        }

        return events.filter { event ->
            selectedCategories.any { category ->
                event.category.equals(category, ignoreCase = true)
            }
        }
    }

    /**
     * Filters news items by the selected categories.
     * If no categories are selected, returns all news items.
     */
    fun filterNews(newsItems: List<NewsItem>, selectedCategories: Set<String>): List<NewsItem> {
        if (selectedCategories.isEmpty()) {
            return newsItems
        }

        return newsItems.filter { newsItem ->
            selectedCategories.any { category ->
                newsItem.category.equals(category, ignoreCase = true)
            }
        }
    }
}
