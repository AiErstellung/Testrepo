import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val composeUiVersion = "1.5.3"
val material3Version = "1.2.0"

val localProperties = gradleLocalProperties(
    rootDir,
    providers)
fun requireLocalProperty(key: String) =
    localProperties[key]?.toString()?.takeIf { it.isNotBlank() }
        ?: error("$key must be defined in local.properties (and kept out of Git)")

fun optionalLocalProperty(key: String): String? =
    localProperties[key]?.toString()?.takeIf { it.isNotBlank() }

val storageEndpoint = requireLocalProperty("ELMON_STORAGE_ENDPOINT")
val storageBucket = requireLocalProperty("ELMON_STORAGE_BUCKET")
val storageRegion = requireLocalProperty("ELMON_STORAGE_REGION")
val storageAccessKey = requireLocalProperty("ELMON_STORAGE_ACCESS_KEY")
val storageSecretKey = requireLocalProperty("ELMON_STORAGE_SECRET_KEY")
val storageSessionToken = optionalLocalProperty("ELMON_STORAGE_SESSION_TOKEN")

android {
    namespace = "com.elmon.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.elmon.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "STORAGE_ENDPOINT", "\"$storageEndpoint\"")
        buildConfigField("String", "STORAGE_BUCKET", "\"$storageBucket\"")
        buildConfigField("String", "STORAGE_REGION", "\"$storageRegion\"")
        buildConfigField("String", "STORAGE_ACCESS_KEY", "\"$storageAccessKey\"")
        buildConfigField("String", "STORAGE_SECRET_KEY", "\"$storageSecretKey\"")
        buildConfigField(
            "String",
            "STORAGE_SESSION_TOKEN",
            storageSessionToken?.let { "\"$it\"" } ?: "null"
        )

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeUiVersion
    }

    packaging {
        resources {
            excludes += setOf("META-INF/AL2.0", "META-INF/LGPL2.1")
        }
    }
}

repositories {
    google()
    mavenCentral()
}

dependencies {

    // Compose Libraries
    implementation("androidx.compose.ui:ui:$composeUiVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeUiVersion")
    implementation("androidx.compose.material3:material3:$material3Version")
    implementation("androidx.compose.foundation:foundation:$composeUiVersion")
    implementation("androidx.compose.material3:material3-window-size-class:$material3Version")
    implementation("androidx.compose.material:material-icons-extended:$composeUiVersion")

    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    implementation("androidx.core:core-ktx:1.11.0")
    implementation("androidx.media3:media3-exoplayer:1.1.1")
    implementation("androidx.media3:media3-ui:1.1.1")

    implementation("com.google.android.material:material:1.10.0")

    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Optional / empfohlen
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

