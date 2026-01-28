package com.faithflow.domain.repository

import com.faithflow.core.network.ApiResult
import com.faithflow.domain.model.NewsItem
import kotlinx.coroutines.flow.StateFlow

interface NewsRepository {
    val news: StateFlow<List<NewsItem>>
    val selectedNews: StateFlow<NewsItem?>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>

    suspend fun loadNews(): ApiResult<Unit>
    suspend fun refreshNews(): ApiResult<Unit>
    suspend fun selectNews(newsId: String)
    fun clearSelection()
    fun getNewsByCategory(category: String): List<NewsItem>
    fun getUrgentNews(): List<NewsItem>
    fun clearError()
}
