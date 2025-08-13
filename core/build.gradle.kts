plugins {
    id("com.android.library")
    alias(libs.plugins.org.jetbrains.kotlin.android)
    id("kotlin-kapt")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.corp.core"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.material)
    testImplementation(libs.junit)

    // DI
    implementation(Deps.hiltAndroid)
    kapt(Deps.hiltAndroidCompiler)
    kapt(Deps.hiltCompiler)
    implementation(Deps.hiltNavigationCompose)

    implementation(Deps.squareOkio)
}

object Deps {

    object Versions {
        const val daggerHilt = "2.49"
        const val okio = "3.7.0"
    }

    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.daggerHilt}"
    const val hiltAndroidCompiler = "com.google.dagger:hilt-compiler:${Versions.daggerHilt}"
    const val hiltCompiler = "androidx.hilt:hilt-compiler:1.0.0"
    const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:1.0.0"

    const val squareOkio = "com.squareup.okio:okio:${Versions.okio}"
}