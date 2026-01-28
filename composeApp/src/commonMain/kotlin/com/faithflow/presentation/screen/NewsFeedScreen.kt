package com.faithflow.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.faithflow.domain.model.NewsItem
import com.faithflow.presentation.components.EmptyView
import com.faithflow.presentation.components.ErrorView
import com.faithflow.presentation.components.LoadingView
import com.faithflow.presentation.viewmodel.NewsFeedViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsFeedScreen(
    viewModel: NewsFeedViewModel = koinViewModel(),
    onBack: () -> Unit,
    onNewsClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News Feed") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                uiState.isLoading && uiState.newsItems.isEmpty() -> {
                    LoadingView()
                }
                uiState.error != null && uiState.newsItems.isEmpty() -> {
                    ErrorView(
                        message = uiState.error!!,
                        onRetry = { viewModel.refreshNewsItems() }
                    )
                }
                uiState.newsItems.isEmpty() -> {
                    EmptyView(message = "No news items found")
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.newsItems, key = { it.id }) { newsItem ->
                            NewsItemCard(
                                newsItem = newsItem,
                                onClick = { onNewsClick(newsItem.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsItemCard(
    newsItem: NewsItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (newsItem.isUrgent) {
                Text(
                    text = "URGENT",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = newsItem.headline,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))

            if (newsItem.category.isNotBlank()) {
                Text(
                    text = newsItem.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = newsItem.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )

            if (newsItem.author.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "By ${newsItem.author}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
