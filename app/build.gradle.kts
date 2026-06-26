plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
}

android {
    namespace   = "com.example.safari"
    compileSdk  = 35

    defaultConfig {
        applicationId = "com.example.safari"
        minSdk        = 31
        targetSdk     = 35
        versionCode   = 1
        versionName   = "1.0"
    }

    // ── Signing ───────────────────────────────────────────────────────────────
    // Reads from environment variables injected by build.yml.
    // Falls back gracefully to debug signing when env vars are absent (local dev).
    signingConfigs {
        val keystorePath     = System.getenv("KEYSTORE_PATH")
        val keystorePassword = System.getenv("KEYSTORE_PASSWORD")
        val keyAlias         = System.getenv("KEY_ALIAS")
        val keyPassword      = System.getenv("KEY_PASSWORD")

        if (keystorePath != null && keystorePassword != null &&
            keyAlias != null     && keyPassword != null) {
            create("release") {
                storeFile     = file(keystorePath)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled    = true
            isShrinkResources  = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Use release signing config if it was created, else fall back to debug
            val releaseCfg = signingConfigs.findByName("release")
            signingConfig  = releaseCfg ?: signingConfigs.getByName("debug")
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable        = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi"
        )
    }

    buildFeatures {
        compose = true
    }

    // Suppress warnings for mixed Compose API opt-ins
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    // Kyant0/AndroidLiquidGlass — AGSL shader-based glass (API 31+)
    implementation(libs.kyant0.backdrop)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
