package com.faithflow.data.repository

import com.faithflow.core.config.GoogleSheetsConfig
import com.faithflow.core.network.ApiResult
import com.faithflow.data.network.GoogleSheetsService
import com.faithflow.domain.model.NewsItem
import com.faithflow.domain.repository.NewsRepository
import com.faithflow.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NewsRepositoryImpl(
    private val googleSheetsService: GoogleSheetsService,
    private val preferencesRepository: PreferencesRepository
) : NewsRepository {

    private val _news = MutableStateFlow<List<NewsItem>>(emptyList())
    private val _selectedNews = MutableStateFlow<NewsItem?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    override val news: StateFlow<List<NewsItem>> = _news.asStateFlow()
    override val selectedNews: StateFlow<NewsItem?> = _selectedNews.asStateFlow()
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    override val error: StateFlow<String?> = _error.asStateFlow()

    override suspend fun loadNews(): ApiResult<Unit> {
        if (_news.value.isNotEmpty()) {
            return ApiResult.Success(Unit)
        }
        return refreshNews()
    }

    override suspend fun refreshNews(): ApiResult<Unit> {
        _isLoading.value = true
        _error.value = null

        return try {
            // For news, use secondary CSV URL (can be configured similarly)
            val fetchedNews = googleSheetsService.fetchNews(GoogleSheetsConfig.NEWS_CSV_URL)
            _news.value = fetchedNews

            _isLoading.value = false
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            _error.value = e.message ?: "Failed to load news"
            ApiResult.Error(e)
        }
    }

    override suspend fun selectNews(newsId: String) {
        val newsItem = _news.value.find { it.id == newsId }
        _selectedNews.value = newsItem

        if (newsItem == null) {
            _error.value = "News item not found: $newsId"
        }
    }

    override fun clearSelection() {
        _selectedNews.value = null
    }

    override fun getNewsByCategory(category: String): List<NewsItem> {
        return _news.value.filter { it.category.equals(category, ignoreCase = true) }
    }

    override fun getUrgentNews(): List<NewsItem> {
        return _news.value.filter { it.isUrgent }
    }

    override fun clearError() {
        _error.value = null
    }
}
