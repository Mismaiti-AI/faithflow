package com.faithflow

import androidx.compose.runtime.*
import com.faithflow.di.allModules
import com.faithflow.presentation.navigation.AppNavigation
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinAppDeclaration


@Composable
fun App(koinAppDeclaration: KoinAppDeclaration? = null) {
    KoinApplication(application = {
        modules(allModules())
        koinAppDeclaration?.invoke(this)
    }) {
        AppTheme {
            AppNavigation()
        }
    }
}
