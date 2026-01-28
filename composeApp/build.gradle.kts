import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    // WASM target for web preview
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    // Create intermediate source set for mobile platforms (Android + iOS)
    // This allows Room to be shared between Android/iOS but not wasmJs
    sourceSets {
        // Mobile-only source set (Android + iOS shared code with Room)
        val mobileMain by creating {
            dependsOn(commonMain.get())
        }

        androidMain {
            dependsOn(mobileMain)
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.androidx.room.sqlite.wrapper)

                implementation(libs.koin.androidx.compose)
            }
        }

        iosMain {
            dependsOn(mobileMain)
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }

        // wasmJs source set - NO Room, uses mock data for preview
        val wasmJsMain by getting {
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }

        commonMain.dependencies {
            // Compose core - works on all platforms including wasmJs
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Core multiplatform libraries
            implementation(libs.kermit)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            // Ktor - core only, engines are platform-specific
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)

            // Koin core DI
            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            // Multiplatform Settings
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)
        }

        // Mobile-only dependencies (Android + iOS) - NOT wasmJs
        // These have savedstate/lifecycle dependencies that don't support wasmJs
        mobileMain.dependencies {
            // Lifecycle & ViewModel
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Navigation
            implementation(libs.androidx.navigation.compose)

            // Koin ViewModel support
            implementation(libs.koin.compose.viewmodel)

            // Coil image loading
            implementation(libs.coil)
            implementation(libs.coil.network.ktor)
            implementation(libs.coil.compose)
            implementation(libs.coil.mp)
            implementation(libs.coil.gif)

            // Room database
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        // Global opt-in for experimental APIs to avoid annotation errors
        all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
            languageSettings.optIn("androidx.compose.material3.ExperimentalMaterial3Api")
            languageSettings.optIn("androidx.compose.foundation.ExperimentalFoundationApi")
        }
    }
}

android {
    namespace = "com.faithflow"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.faithflow"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.faithflow"
    generateResClass = always
}
