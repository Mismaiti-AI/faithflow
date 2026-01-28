package com.faithflow.presentation.screens.news

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.faithflow.presentation.components.EmptyView

/**
 * News Feed screen - displays church news and announcements
 */
@Composable
fun NewsFeedScreen(
    onBack: () -> Unit,
    onNewsClick: (String) -> Unit
) {
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
        EmptyView(
            message = "News articles will appear here once Google Sheets is configured",
            modifier = Modifier.padding(padding)
        )
    }
}
