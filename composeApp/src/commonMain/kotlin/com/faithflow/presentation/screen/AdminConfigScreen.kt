package com.faithflow.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.faithflow.presentation.viewmodel.AdminConfigUiState
import com.faithflow.presentation.viewmodel.AdminConfigViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminConfigScreen(
    viewModel: AdminConfigViewModel = koinViewModel(),
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetUrl by viewModel.sheetUrl.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configure Google Sheets") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Enter the URL of your Google Sheets document",
                style = MaterialTheme.typography.bodyLarge
            )

            OutlinedTextField(
                value = sheetUrl,
                onValueChange = { viewModel.updateSheetUrl(it) },
                label = { Text("Google Sheets URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = uiState !is AdminConfigUiState.Testing
            )

            when (uiState) {
                is AdminConfigUiState.Testing -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Testing connection...")
                    }
                }
                is AdminConfigUiState.Success -> {
                    Text(
                        text = (uiState as AdminConfigUiState.Success).message,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = onSuccess,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue")
                    }
                }
                is AdminConfigUiState.Error -> {
                    Text(
                        text = (uiState as AdminConfigUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = { viewModel.testConnection() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Test Connection")
                    }
                }
                is AdminConfigUiState.Idle -> {
                    Button(
                        onClick = { viewModel.testConnection() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = sheetUrl.isNotBlank()
                    ) {
                        Text("Test Connection")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tip: Make sure your Google Sheets is published to the web",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
