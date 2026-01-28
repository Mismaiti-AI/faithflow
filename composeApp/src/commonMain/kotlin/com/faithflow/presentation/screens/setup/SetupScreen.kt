package com.faithflow.presentation.screens.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.faithflow.domain.repository.PreferencesRepository
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Setup screen for configuring Google Sheets URL
 * This is shown on first launch or when configuration is needed
 */
@Composable
fun SetupScreen(
    onSetupComplete: () -> Unit,
    preferencesRepository: PreferencesRepository = koinInject()
) {
    var sheetUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Setup FaithFlow") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Welcome to FaithFlow!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "To get started, please enter your Google Sheets URL.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = sheetUrl,
                onValueChange = { sheetUrl = it },
                label = { Text("Google Sheets URL") },
                placeholder = { Text("https://docs.google.com/spreadsheets/d/...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Instructions:\n" +
                        "1. Create a Google Sheet with tabs for Events, News, and ChurchProfile\n" +
                        "2. Go to File → Share → Publish to web\n" +
                        "3. Select 'Events' tab and publish as CSV\n" +
                        "4. Copy the URL and paste it above",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        preferencesRepository.saveSheetUrl(sheetUrl)
                        isLoading = false
                        onSetupComplete()
                    }
                },
                enabled = sheetUrl.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoading) "Saving..." else "Continue")
            }
        }
    }
}
