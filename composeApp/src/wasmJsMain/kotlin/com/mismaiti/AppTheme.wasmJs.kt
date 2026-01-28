package com.faithflow

import androidx.compose.runtime.Composable

/**
 * wasmJs implementation of SystemAppearance.
 *
 * On web, we don't have native system appearance APIs like Android/iOS.
 * This is a no-op implementation. For a more complete implementation,
 * you could use CSS media queries or JavaScript to detect/set dark mode.
 */
@Composable
internal actual fun SystemAppearance(isDark: Boolean) {
    // No-op on wasmJs - web browsers handle dark mode via CSS/media queries
    // Could potentially use js("...") to set document.body.classList for theming
}
