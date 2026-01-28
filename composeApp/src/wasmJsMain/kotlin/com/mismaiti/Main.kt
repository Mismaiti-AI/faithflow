package com.faithflow

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

/**
 * wasmJs entry point for Compose Multiplatform.
 *
 * This renders the app using ComposeViewport which supports
 * HTML interop and accessibility features.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val body = document.body ?: return
    ComposeViewport(body) {
        App()
    }
}
