package com.faithflow

/**
 * Mobile-specific code shared between Android and iOS.
 *
 * This source set contains:
 * - Room database entities, DAOs, and database classes
 * - Any other code that requires mobile-only dependencies
 *
 * wasmJs preview does NOT include this code - it uses mock implementations instead.
 */

// Marker interface for mobile-only components
interface MobilePlatform
