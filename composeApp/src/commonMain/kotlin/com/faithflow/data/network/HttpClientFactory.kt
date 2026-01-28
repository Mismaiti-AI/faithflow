package com.faithflow.data.network

import io.ktor.client.HttpClient

/**
 * Factory pattern for creating platform-specific HttpClient instances
 * Each platform (Android/iOS) provides its own engine implementation
 */
expect class HttpClientFactory() {
    fun create(): HttpClient
}
