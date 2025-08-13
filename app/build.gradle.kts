plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.corp.strikesimple"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.corp.strikesimple"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        compose = true
    }
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(Deps.material3)
    implementation(libs.androidx.paging.compose.android)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)

    //Test
    androidTestImplementation(libs.mockk.android)
    testImplementation(libs.mockk)
    testImplementation(Deps.testCoroutines)
    testImplementation(Deps.turbine)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0")

    implementation(project(":core"))
    implementation(project(":data"))

    // DI
    implementation(Deps.hiltAndroid)
    kapt(Deps.hiltAndroidCompiler)
    kapt(Deps.hiltCompiler)
    implementation(Deps.hiltNavigationCompose)

    // DB
    implementation(Deps.runTime)
    annotationProcessor(Deps.compiler)
    implementation(Deps.roomKtx)
    implementation(Deps.rxJava2)
    implementation(Deps.rxJava3)
    implementation(Deps.guava)
    implementation(Deps.paging)
    kapt(Deps.compiler)
    testImplementation(Deps.testing)

    // Animations
    implementation(Deps.lottieAnimations)
    implementation(Deps.lottieCompose)

    // Lifecycle
    implementation(Deps.vmLifecycle)
    implementation(Deps.vmLifecycleCompose)
    implementation(Deps.vmLifecycleSavedState)
    implementation(Deps.lifecycleLiveData)
    implementation(Deps.lifecycleRuntime)
    implementation(Deps.lifecycleUtilCompose)
    implementation(Deps.lifecycleVmKtx)
    kapt(Deps.lifecycleCompiler)
    testImplementation(kotlin("test"))


}

object Deps {

    object Versions {
        const val daggerHilt = "2.49"
        const val roomVersion = "2.6.1"
        const val lifecycle = "2.6.2"
        const val lottieVersion = "6.0.0"
        const val lottieComposeVersion = "4.0.0"
        const val material3 = "1.2.1"
        const val test = "1.8.1"
        const val turbine = "1.0.0"
    }

    const val material3 = "androidx.compose.material3:material3:${Versions.material3}"

    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.daggerHilt}"
    const val hiltAndroidCompiler = "com.google.dagger:hilt-compiler:${Versions.daggerHilt}"
    const val hiltCompiler = "androidx.hilt:hilt-compiler:1.0.0"
    const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:1.0.0"

    const val runTime = "androidx.room:room-runtime:${Versions.roomVersion}"
    const val compiler = "androidx.room:room-compiler:${Versions.roomVersion}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.roomVersion}"
    const val rxJava2 = "androidx.room:room-rxjava2:${Versions.roomVersion}"
    const val rxJava3 = "androidx.room:room-rxjava3:${Versions.roomVersion}"
    const val guava = "androidx.room:room-guava:${Versions.roomVersion}"
    const val testing = "androidx.room:room-testing:${Versions.roomVersion}"
    const val paging = "androidx.room:room-paging:${Versions.roomVersion}"

    const val lottieAnimations = "com.airbnb.android:lottie:${Versions.lottieVersion}"
    const val lottieCompose = "com.airbnb.android:lottie-compose:${Versions.lottieComposeVersion}"

    const val vmLifecycle = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val vmLifecycleCompose =
        "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle}"
    const val lifecycleVmKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
    const val lifecycleUtilCompose =
        "androidx.lifecycle:lifecycle-runtime-compose:${Versions.lifecycle}"
    const val vmLifecycleSavedState =
        "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifecycle}"
    const val lifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}"

    const val testCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.test}"
    const val turbine = "app.cash.turbine:turbine:${Versions.turbine}"
}