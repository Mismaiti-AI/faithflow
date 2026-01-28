package com.faithflow.domain.repository

import com.faithflow.core.network.ApiResult
import com.faithflow.domain.model.NewsItem
import kotlinx.coroutines.flow.StateFlow

interface NewsItemRepository {
    val newsItems: StateFlow<List<NewsItem>>
    val selectedNewsItem: StateFlow<NewsItem?>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>

    suspend fun loadNewsItems(): ApiResult<Unit>
    suspend fun refreshNewsItems(): ApiResult<Unit>
    suspend fun selectNewsItem(newsItemId: String)
    fun clearSelection()
    suspend fun getNewsItemById(id: String): ApiResult<NewsItem>
    fun clearError()
}
