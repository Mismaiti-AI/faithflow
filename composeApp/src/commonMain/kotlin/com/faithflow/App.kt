package com.faithflow

import androidx.compose.runtime.*
import com.faithflow.di.appModule
import com.faithflow.di.databaseModule
import com.faithflow.di.networkModule
import com.faithflow.di.platformModule
import com.faithflow.presentation.navigation.AppNavigation
import com.faithflow.presentation.theme.AppTheme
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinAppDeclaration

@Composable
fun App(koinAppDeclaration: KoinAppDeclaration? = null) {
    KoinApplication(application = {
        modules(
            listOf(
                appModule,
                networkModule,
                databaseModule,
                platformModule()
            )
        )
        koinAppDeclaration?.invoke(this)
    }) {
        AppTheme {
            AppNavigation()
        }
    }
}
