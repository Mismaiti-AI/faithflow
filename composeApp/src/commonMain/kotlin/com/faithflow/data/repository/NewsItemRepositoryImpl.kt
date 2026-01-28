package com.faithflow.data.repository

import com.faithflow.core.network.ApiResult
import com.faithflow.data.local.dao.NewsItemDao
import com.faithflow.data.local.entity.toDomain
import com.faithflow.data.local.entity.toEntity
import com.faithflow.data.remote.GoogleSheetsService
import com.faithflow.domain.model.NewsItem
import com.faithflow.domain.repository.NewsItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsItemRepositoryImpl(
    private val googleSheetsService: GoogleSheetsService,
    private val newsItemDao: NewsItemDao,
    private val sheetUrl: () -> String // Function to get current sheet URL
) : NewsItemRepository {

    private val _newsItems = MutableStateFlow<List<NewsItem>>(emptyList())
    private val _selectedNewsItem = MutableStateFlow<NewsItem?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    override val newsItems: StateFlow<List<NewsItem>> = _newsItems.asStateFlow()
    override val selectedNewsItem: StateFlow<NewsItem?> = _selectedNewsItem.asStateFlow()
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    override val error: StateFlow<String?> = _error.asStateFlow()

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        repositoryScope.launch {
            newsItemDao.observeAll().collect { entities ->
                _newsItems.value = entities.map { it.toDomain() }
            }
        }
    }

    override suspend fun loadNewsItems(): ApiResult<Unit> {
        if (_newsItems.value.isNotEmpty()) {
            return ApiResult.Success(Unit)
        }
        return refreshNewsItems()
    }

    override suspend fun refreshNewsItems(): ApiResult<Unit> {
        _isLoading.value = true
        _error.value = null

        return try {
            val url = sheetUrl()
            if (url.isBlank()) {
                throw Exception("Google Sheets URL not configured")
            }

            val newsItems = googleSheetsService.fetchNewsItems(url)
            newsItemDao.replaceAll(newsItems.map { it.toEntity() })

            _isLoading.value = false
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false

            val cached = newsItemDao.getAll()
            if (cached.isNotEmpty()) {
                _newsItems.value = cached.map { it.toDomain() }
                _error.value = "Using cached data. ${e.message}"
                ApiResult.Success(Unit)
            } else {
                _error.value = e.message ?: "Failed to load news items"
                ApiResult.Error(e)
            }
        }
    }

    override suspend fun selectNewsItem(newsItemId: String) {
        val newsItem = _newsItems.value.find { it.id == newsItemId }
        _selectedNewsItem.value = newsItem
    }

    override fun clearSelection() {
        _selectedNewsItem.value = null
    }

    override suspend fun getNewsItemById(id: String): ApiResult<NewsItem> {
        val cached = _newsItems.value.find { it.id == id }
        if (cached != null) {
            return ApiResult.Success(cached)
        }

        val local = newsItemDao.getById(id)
        return if (local != null) {
            ApiResult.Success(local.toDomain())
        } else {
            ApiResult.Error(Exception("News item not found: $id"))
        }
    }

    override fun clearError() {
        _error.value = null
    }
}
