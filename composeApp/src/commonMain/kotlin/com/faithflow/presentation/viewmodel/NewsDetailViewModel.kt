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

class NewsDetailViewModel(
    private val newsItemId: String,
    private val repository: NewsItemRepository
) : ViewModel() {

    val uiState: StateFlow<NewsDetailUiState> = combine(
        repository.selectedNewsItem,
        repository.isLoading,
        repository.error
    ) { newsItem, isLoading, error ->
        NewsDetailUiState(
            newsItem = newsItem,
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NewsDetailUiState(isLoading = true)
    )

    init {
        loadNewsItem()
    }

    private fun loadNewsItem() {
        viewModelScope.launch {
            repository.selectNewsItem(newsItemId)
        }
    }

    fun clearError() {
        repository.clearError()
    }

    override fun onCleared() {
        super.onCleared()
        repository.clearSelection()
    }
}

data class NewsDetailUiState(
    val newsItem: NewsItem? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
