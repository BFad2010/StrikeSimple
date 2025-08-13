plugins {
    id("com.android.library")
    alias(libs.plugins.org.jetbrains.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.corp.data"
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
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

dependencies {
    implementation(libs.material)
    testImplementation(libs.junit)

    //DB
    implementation(Room.runTime)
    annotationProcessor(Room.compiler)
    implementation(Room.roomKtx)
    implementation(Room.rxJava2)
    implementation(Room.rxJava3)
    implementation(Room.guava)
    implementation(Room.paging)
    kapt(Room.compiler)
    testImplementation(Room.testing)
    implementation(Room.gson)
    // DI
    implementation(Hilt.hiltAndroid)
    kapt(Hilt.hiltAndroidCompiler)
    kapt(Hilt.hiltCompiler)
    implementation(Hilt.hiltNavigationCompose)
}

object Hilt {

    object Versions {
        const val daggerHilt = "2.49"
    }
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.daggerHilt}"
    const val hiltAndroidCompiler = "com.google.dagger:hilt-compiler:${Versions.daggerHilt}"
    const val hiltCompiler = "androidx.hilt:hilt-compiler:1.0.0"
    const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:1.0.0"
}

object Room {
    object Versions {
        const val roomVersion = "2.6.1"
        const val gson = "2.6.2"
    }

    const val runTime = "androidx.room:room-runtime:${Versions.roomVersion}"
    const val compiler = "androidx.room:room-compiler:${Versions.roomVersion}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.roomVersion}"
    const val rxJava2 = "androidx.room:room-rxjava2:${Versions.roomVersion}"
    const val rxJava3 = "androidx.room:room-rxjava3:${Versions.roomVersion}"
    const val guava = "androidx.room:room-guava:${Versions.roomVersion}"
    const val testing = "androidx.room:room-testing:${Versions.roomVersion}"
    const val paging = "androidx.room:room-paging:${Versions.roomVersion}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
}