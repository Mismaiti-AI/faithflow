package com.faithflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithflow.domain.model.NewsItem
import com.faithflow.domain.repository.NewsItemRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NewsFeedViewModel(
    private val repository: NewsItemRepository
) : ViewModel() {

    val uiState: StateFlow<NewsFeedUiState> = combine(
        repository.newsItems,
        repository.isLoading,
        repository.error
    ) { newsItems, isLoading, error ->
        NewsFeedUiState(
            newsItems = newsItems,
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NewsFeedUiState()
    )

    init {
        loadNewsItems()
    }

    fun loadNewsItems() {
        viewModelScope.launch {
            repository.loadNewsItems()
        }
    }

    fun refreshNewsItems() {
        viewModelScope.launch {
            repository.refreshNewsItems()
        }
    }

    fun clearError() {
        repository.clearError()
    }
}

data class NewsFeedUiState(
    val newsItems: List<NewsItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
